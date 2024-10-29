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
import io.netty.channel.*;
//import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {
	private final RtcpStack stack;
	private final ConcurrentHashMap<String, NetworkLink> links = new ConcurrentHashMap<>();

	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;

	private boolean isServerStarted = false;

	private static final int BUFFER_SIZE = 256 * 1024 * 10;
	public static Logger logger = LogManager.getLogger(NetworkManager.class);

	public NetworkManager(RtcpStack stack) {
		this.stack = stack;

		this.bossGroup = ServerChannelUtils.createEventLoopGroup();
		this.workerGroup = ServerChannelUtils.createEventLoopGroup();
	}

	public void addLink(
			String linkId,
			InetAddress remoteAddress,
			int remotePort,
			InetAddress localAddress,
			int localPort
	) {
		NetworkLink link = getLinkByLinkId(linkId);
		if (link != null) return;

		link = new NetworkLink(linkId, remoteAddress, remotePort, localAddress, localPort, this);
		this.links.put(linkId, link);
	}

	public NetworkLink getLinkByLinkId(String linkId) {
		return links.get(linkId);
	}

	public NetworkLink getLinkByPort(int port) {
		for (NetworkLink link : links.values()) {
			if (link.getRemotePort() == port) {
				return link;
			}
		}
		return null;
	}

	private void setStreamServer(NetworkLink link) {
		ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group(bossGroup, workerGroup);

		bootstrap.channel(ServerChannelUtils.getSocketChannel())
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) {
						NetworkLink foundLink = getLinkByPort(socketChannel.remoteAddress().getPort());
						if(foundLink != null) {
							foundLink.setChannel(socketChannel);
						} else {
							socketChannel.close();
						}
						socketChannel.pipeline().addLast(new StreamChannelInitializer(stack));
					}
				})
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		ChannelFuture future = bootstrap.bind(link.getLocalPort()).syncUninterruptibly();
		future.awaitUninterruptibly();

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

		if (this.stack.isLogging) logger.debug("Server started on port " + link.getLocalPort());
		this.isServerStarted = true;
	}

	private void setDatagramServer(NetworkLink link) {
		Bootstrap connectionlessBootstrap = new Bootstrap();
		EventLoopGroup group = ServerChannelUtils.createEventLoopGroup();
		connectionlessBootstrap.channel(ServerChannelUtils.getDatagramChannel());

		connectionlessBootstrap.group(group)
				.option(EpollChannelOption.SO_SNDBUF, BUFFER_SIZE)
				.option(EpollChannelOption.SO_RCVBUF, BUFFER_SIZE);
	//.option(EpollChannelOption.SO_RCVBUF)

		connectionlessBootstrap
				.handler(new DatagramChannelInitializer(stack));
		ChannelFuture future = connectionlessBootstrap.bind(link.getLocalAddress(), link.getLocalPort()).syncUninterruptibly();
		future.awaitUninterruptibly();
		link.setChannel(future.channel());

		for (int i = 0; i < stack.getThreadPoolSize(); ++i) {
			future = connectionlessBootstrap.bind(new InetSocketAddress("0.0.0.0", link.getLocalPort()));

			future.awaitUninterruptibly();
			if(!future.isSuccess()) {
				logger.debug("Channel Not Connected:" + future.cause());
			}

//			serverChannels.add(future.channel());
		}
	}

	public void setStreamClient(NetworkLink link) {
		NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap().group(group)
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
		if (!future.isSuccess()) {
			System.out.println(future.cause());
			return;
		}

		new Thread(() -> {
			try {
				link.getChannel().closeFuture().syncUninterruptibly();
			} finally {
				group.shutdownGracefully();
			}
		}).start();
	}

	public void setDatagramClient(NetworkLink link) {
		EventLoopGroup group = new EpollEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.channel(EpollDatagramChannel.class)
				.option(EpollChannelOption.SO_SNDBUF, BUFFER_SIZE)
				.option(EpollChannelOption.SO_RCVBUF, BUFFER_SIZE)
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

	public void startLink(String linkId) {
		NetworkLink link = this.getLinkByLinkId(linkId);
		if (link == null) return;

		TransportEnum transport = stack.transport;

		if (stack.isServer) {
			if (!isServerStarted) {
				if (transport == TransportEnum.TCP) this.setStreamServer(link);
				else this.setDatagramServer(link);
			}
		} else {
			if (transport == TransportEnum.TCP) {
				this.setStreamClient(link);
			} else {
				this.setDatagramClient(link);
			}
		}
	}

	public void stopLink(String linkId) {
		NetworkLink link = getLinkByLinkId(linkId);
		if (linkId == null) {
			return;
		}
		link.getChannel().close();
	}

	public void sendMessage(RtcpBasePacket message, int port, AsyncCallback callback) {
		NetworkLink link = getLinkByPort(port);

		ChannelFuture result = stack.transport == TransportEnum.UDP ?
				link.getChannel().writeAndFlush(new DatagramPacket(RtcpParser.encode(message), new InetSocketAddress(link.getRemoteAddress(), link.getRemotePort()))) :
				link.getChannel().writeAndFlush((RtcpParser.encode(message)));

		result.awaitUninterruptibly();
		if (result.isSuccess()) {
			callback.onSuccess();
		} else {
			callback.onError(new Exception("Message is not sent"));
		}
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
