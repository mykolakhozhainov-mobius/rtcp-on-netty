package edu.rtcp.server.network;

import edu.rtcp.RtcpStack;
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
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Map;
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

	public void addLink(String linkId, InetAddress remoteAddress, int remotePort, InetAddress localAddress,
						int localPort) {
		NetworkLink link = getLinkByLinkId(linkId);
		if (link == null) {
			link = new NetworkLink(linkId, remoteAddress, remotePort, localAddress, localPort, this);
			links.put(linkId, link);
		}
	}

	public NetworkLink getLinkByLinkId(String linkId) {
		return links.get(linkId);
	}

	public NetworkLink getLinkByAddress(InetSocketAddress address) {
		for (NetworkLink link : links.values()) {
			if (new InetSocketAddress (link.getRemoteAddress(), link.getRemotePort()).equals(address)) {
				return link;
			}
		}
		return null;
	}

	public void startLink(String linkId) {
		NetworkLink link = this.getLinkByLinkId(linkId);
		if (link == null) {
			return;
		}
		if (stack.isServer) {
				if (stack.transport.equals(TransportEnum.TCP)) {
					ServerBootstrap bootstrap = new ServerBootstrap()
							.group(bossGroup, workerGroup)
							.channel(ServerChannelUtils.getSocketChannel())
							.childHandler(new ChannelInitializer<SocketChannel>() {
								@Override
								protected void initChannel(SocketChannel socketChannel) {
									NetworkLink foundLink = getLinkByAddress(socketChannel.remoteAddress());
									if(foundLink != null) {
										link.setChannel(socketChannel);
									} else {
										socketChannel.close();
									}
									socketChannel.pipeline().addLast(new StreamChannelInitializer(stack));
								}
							});

					ChannelFuture future = bootstrap.bind(link.getLocalPort())
							.syncUninterruptibly()
							.awaitUninterruptibly();

					new Thread(() -> {
						try {
							future.channel().closeFuture().sync();
						} catch (InterruptedException e) {
							System.out.println(e);
						} finally {
							workerGroup.shutdownGracefully();
							bossGroup.shutdownGracefully();
						}
					}).start();
					link.setChannel(future.channel());
				} 
				else if (stack.transport.equals(TransportEnum.UDP)) {
					EventLoopGroup group = ServerChannelUtils.createEventLoopGroup();
					Bootstrap bootstrap = new Bootstrap()
							.channel(ServerChannelUtils.getDatagramChannel())
							.group(group)
							.option(EpollChannelOption.SO_REUSEPORT, true)
							.option(EpollChannelOption.IP_RECVORIGDSTADDR, true)
							.option(ChannelOption.SO_SNDBUF, 256*1024)
							.option(ChannelOption.SO_RCVBUF, 256*1024)
							.option(EpollChannelOption.IP_FREEBIND, true)
							.handler(new DatagramChannelInitializer(stack));

					ChannelFuture future = bootstrap
							.bind(link.getLocalAddress(), link.getLocalPort())
							.syncUninterruptibly()
							.awaitUninterruptibly();
					link.setChannel(future.channel());
			}
		} 
		else {
			if (stack.transport.equals(TransportEnum.TCP)) {
				NioEventLoopGroup group = new NioEventLoopGroup();
				Bootstrap bootstrap = new Bootstrap()
						.group(group)
						.channel(NioSocketChannel.class)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel socketChannel) {
								link.setChannel(socketChannel);
								socketChannel.pipeline().addLast(new StreamChannelInitializer(stack));
							}
						});
				
				ChannelFuture future = bootstrap
						.connect(new InetSocketAddress(link.getRemoteAddress(), link.getRemotePort()),
								new InetSocketAddress(link.getLocalAddress(), link.getLocalPort()))
						.syncUninterruptibly();
//				link.setChannel(future.channel());
				new Thread(() -> {
					try {
						link.getChannel().closeFuture().syncUninterruptibly();
					} finally {
						group.shutdownGracefully();
					}
				}).start();
			} 
			else if (stack.transport.equals(TransportEnum.UDP)) {
				EventLoopGroup group = new EpollEventLoopGroup();
				Bootstrap bootstrap = new Bootstrap()
						.option(EpollChannelOption.IP_RECVORIGDSTADDR, true)
						.option(ChannelOption.SO_SNDBUF, 256*1024)
						.option(ChannelOption.SO_RCVBUF, 256*1024)
						.option(EpollChannelOption.IP_FREEBIND, true)
						.group(group).channel(EpollDatagramChannel.class)
						.handler(new DatagramChannelInitializer(stack));
				
				ChannelFuture future = bootstrap.connect(
						new InetSocketAddress(link.getRemoteAddress(), link.getRemotePort()),
						new InetSocketAddress(link.getLocalAddress(), link.getLocalPort()));
				link.setChannel(future.channel());
				new Thread(() -> {
					try {
						future.channel().closeFuture().sync();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					} finally {
						group.shutdownGracefully();
					}
				}).start();
			}
		}
	}
	
	public void startAllLinks (){
		if (stack.isServer) {
			startLink(links.keys().nextElement());
		} else {
			for (NetworkLink link : links.values()) {
				startLink(link.getLinkId());
			}
		}
	}
	
	public NetworkLink getNextLink() {
		ArrayList<NetworkLink> linksArray = new ArrayList<NetworkLink>(links.values());
		return linksArray.get(linkIndex.incrementAndGet() % links.size());
	}

	public void stopLink(String linkId) {
		NetworkLink link = getLinkByLinkId(linkId);
		if (linkId == null) {
			return;
		}
		link.getChannel().close();
	}

	public void sendMessage(RtcpBasePacket message, InetSocketAddress address, AsyncCallback callback) {
		NetworkLink link = null;
		if (address == null) {
			link = getNextLink();
			
			if (stack.transport == TransportEnum.UDP ) {
				link.getChannel().writeAndFlush(new DatagramPacket(RtcpParser.encode(message), new InetSocketAddress(link.getRemoteAddress(), link.getRemotePort())));
			}
			else {
				link.getChannel().writeAndFlush((RtcpParser.encode(message)));
			}
		}
		else {
			link = getLinkByAddress(address);
			if (stack.transport == TransportEnum.UDP ) {
				link.getChannel().writeAndFlush(new DatagramPacket(RtcpParser.encode(message), (InetSocketAddress) address));
			}
			else {
				link.getChannel().writeAndFlush((RtcpParser.encode(message)));
			}
		}
		

		callback.onSuccess();
	}

	public void stop() {
		if (!links.isEmpty()) {
			for (Map.Entry<String, NetworkLink> currEntry : links.entrySet()) {
				try {
					stopLink(currEntry.getKey());
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
	}
}
