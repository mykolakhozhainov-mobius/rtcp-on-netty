package edu.rtcp.server.network;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.ClientChannelUtils;
import edu.rtcp.common.ServerChannelUtils;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.parser.RtcpParser;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.channel.DatagramChannelInitializer;
import edu.rtcp.server.network.channel.StreamChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.bootstrap.ServerBootstrap;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkManager {
	private final RtcpStack stack;
	private final ConcurrentHashMap<String, NetworkLink> links = new ConcurrentHashMap<String, NetworkLink>();
	private AtomicInteger linkIndex = new AtomicInteger();

	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;

	Boolean isServerStarted = false;

	public NetworkManager(RtcpStack stack) {
		this.stack = stack;
		bossGroup = ServerChannelUtils.createEventLoopGroup();
		workerGroup = ServerChannelUtils.createEventLoopGroup();
	}

	public void addLink(String linkId, InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
		NetworkLink link = stack.isServer ? getLinkByAddress(remoteAddress) : getLinkByAddress(localAddress);
		if (link == null) {
			links.put(linkId, new NetworkLink(linkId, remoteAddress, localAddress));
		}
	}

	public NetworkLink getLinkByAddress(InetSocketAddress address) {
		System.out.println("remoteAddress isServer: " + stack.isServer);
		System.out.println("links Size: " + links.size());
		System.out.println("remoteAddress " + address);
		for (NetworkLink link : links.values()) {
			System.out.println("remoteAddress of link" + link.getRemoteAddress());
			if (link.getRemoteAddress().equals(address)) {
				System.out.println("remoteAddress returned");
				return link;
			}
		}

		return null;
	}

	public void startServer(NetworkLink link) {
		if (stack.transport == TransportEnum.TCP) {
			ServerBootstrap streamBootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
					.channel(ServerChannelUtils.getSocketChannel()).option(ChannelOption.SO_SNDBUF, 256 * 1024)
					.option(ChannelOption.SO_RCVBUF, 256 * 1024).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) {
							NetworkLink foundLink = getLinkByAddress(socketChannel.remoteAddress());
							if (foundLink != null) {
								foundLink.setChannel(socketChannel);
							} else {
								socketChannel.close();
							}
							socketChannel.pipeline().addLast(new StreamChannelInitializer(stack));
						}
					});

			ChannelFuture streamFuture = streamBootstrap.bind(link.getLocalAddress()).syncUninterruptibly()
					.awaitUninterruptibly();

			new Thread(() -> {
				try {
					streamFuture.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					System.out.println(e);
				} finally {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
				}
			}).start();

			for (NetworkLink foundLink : links.values()) {
				foundLink.setChannel(streamFuture.channel());
			}
		} else if (stack.transport == TransportEnum.UDP) {
			Bootstrap datagramBootstrap = new Bootstrap().channel(ServerChannelUtils.getDatagramChannel())
					.group(workerGroup).option(EpollChannelOption.SO_REUSEPORT, true)
					.option(EpollChannelOption.IP_RECVORIGDSTADDR, true).option(ChannelOption.SO_SNDBUF, 256 * 1024)
					.option(ChannelOption.SO_RCVBUF, 256 * 1024).option(EpollChannelOption.IP_FREEBIND, true)
					.handler(new DatagramChannelInitializer(stack));

			ChannelFuture datagramFuture = datagramBootstrap.bind(link.getLocalAddress()).syncUninterruptibly()
					.awaitUninterruptibly();

			for (NetworkLink foundLink : links.values()) {
				foundLink.setChannel(datagramFuture.channel());
			}
		}
	}

	public void startClient(NetworkLink link) {
		EventLoopGroup group = ClientChannelUtils.createEventLoopGroup();
		if (stack.transport == TransportEnum.TCP) {
			Bootstrap bootstrap = new Bootstrap().group(group).channel(ClientChannelUtils.getSocketChannel())
					.option(ChannelOption.SO_SNDBUF, 256 * 1024).option(ChannelOption.SO_RCVBUF, 256 * 1024)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) {
							link.setChannel(socketChannel);
							socketChannel.pipeline().addLast(new StreamChannelInitializer(stack));
						}
					});

			bootstrap.connect(link.getRemoteAddress(), link.getLocalAddress()).syncUninterruptibly();

			new Thread(() -> {
				try {
					link.getChannel().closeFuture().syncUninterruptibly();
				} finally {
					group.shutdownGracefully();
				}
			}).start();

		} else if (stack.transport == TransportEnum.UDP) {
			Bootstrap bootstrap = new Bootstrap().option(EpollChannelOption.IP_RECVORIGDSTADDR, true)
					.option(ChannelOption.SO_SNDBUF, 256 * 1024).option(ChannelOption.SO_RCVBUF, 256 * 1024)
					.option(EpollChannelOption.IP_FREEBIND, true).group(new EpollEventLoopGroup())
					.channel(ClientChannelUtils.getDatagramChannel()).handler(new DatagramChannelInitializer(stack));

			link.setChannel(bootstrap.connect(link.getRemoteAddress(), link.getLocalAddress()).channel());
			new Thread(() -> {
				try {
					link.getChannel().closeFuture().sync();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					group.shutdownGracefully();
				}
			}).start();
		}
	}

	private void startLink(NetworkLink link) {
		if (link == null) {
			return;
		}
		if (stack.isServer) {
			startServer(link);
		} else {
			startClient(link);
		}
	}

	public void startAllLinks() {
		if (stack.isServer) {
			startLink(links.values().iterator().next());
		} else {
			for (NetworkLink link : links.values()) {
				startLink(link);
			}
		}
	}

	public NetworkLink getNextLink() {
		ArrayList<NetworkLink> linksArray = new ArrayList<NetworkLink>(links.values());
		return linksArray.get(linkIndex.incrementAndGet() % links.size());
	}

	public void stopLink(NetworkLink link) {
		link.getChannel().closeFuture();
	}

	public void sendMessage(RtcpBasePacket message, InetSocketAddress address, AsyncCallback callback) {
		NetworkLink link = null;
		if (address == null) {
			link = getNextLink();

			if (stack.transport == TransportEnum.UDP) {
				link.getChannel()
						.writeAndFlush(new DatagramPacket(RtcpParser.encode(message), link.getRemoteAddress()));
			} else {
				link.getChannel().writeAndFlush((RtcpParser.encode(message)));
			}
		} else {
			link = getLinkByAddress(address);
			if (stack.transport == TransportEnum.UDP) {
				link.getChannel().writeAndFlush(new DatagramPacket(RtcpParser.encode(message), address));
			} else {
				link.getChannel().writeAndFlush((RtcpParser.encode(message)));
			}
		}

		callback.onSuccess();
	}

	public void stop() {
		if (!links.isEmpty()) {

			for (NetworkLink link : links.values()) {
				try {
					stopLink(link);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
	}
}
