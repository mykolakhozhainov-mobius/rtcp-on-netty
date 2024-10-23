package edu.rtcp.server.network;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.ServerChannelUtils;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.parser.RtcpParser;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.processor.transport.StreamChannelInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {
	private final RtcpStack stack;
	private final ConcurrentHashMap<String, NetworkLink> links = new ConcurrentHashMap<String, NetworkLink>();

	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;

	private final PendingStorage pendingStorage;

	public NetworkManager(RtcpStack stack) {
		this.stack = stack;
		this.bossGroup = ServerChannelUtils.createEventLoopGroup();
		this.workerGroup = ServerChannelUtils.createEventLoopGroup();

		this.pendingStorage = new PendingStorage();
	}

	public PendingStorage getPendingStorage() {
		return this.pendingStorage;
	}

	public void addLink(
			String linkId,
			InetAddress remoteAddress,
			int remotePort,
			InetAddress localAddress,
			int localPort
	) {
		NetworkLink link = getLinkByLinkId(linkId);
		if (link == null) {
			link = new NetworkLink(linkId, remoteAddress, remotePort, localAddress, localPort, this);
			links.put(linkId, link);
		}
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

	public void startServer(String linkId) {
		NetworkLink link = this.getLinkByLinkId(linkId);
		if (link == null) {
			return;
		}

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();

			bootstrap.group(bossGroup, workerGroup);

			bootstrap.channel(ServerChannelUtils.getSocketChannel())
					.childHandler(new StreamChannelInitializer(stack))
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			link.setChannel(bootstrap.bind(link.getLocalPort()).await().channel());

			new Thread(() -> {
				try {
					link.getChannel().closeFuture().sync();
				} catch (InterruptedException e) {
					System.out.println(e);
				} finally {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
				}
			}).start();
			System.out.println("[TCP-PROCESSOR] Server started on port " + link.getLocalPort());
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	public void startLink(String linkId) {
		NetworkLink link = this.getLinkByLinkId(linkId);
		if (link == null) {
			return;
		}

		NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) {
						link.setChannel(socketChannel);
						socketChannel.pipeline().addLast(new StreamChannelInitializer(stack));
					}
				});
		ChannelFuture future = bootstrap
				.connect(new InetSocketAddress(link.getRemoteAddress(), link.getRemotePort()),
						new InetSocketAddress(link.getLocalAddress(), this.stack.isServer ? 5050: 3030))
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

	public void stopLink(String linkId) {
		NetworkLink link = getLinkByLinkId(linkId);
		if (linkId == null) {
			return;
		}
		link.getChannel().close();
	}

	public void sendMessage(RtcpBasePacket message, int port, AsyncCallback callback) {
		NetworkLink link = getLinkByPort(port);

		if (link == null) {
			callback.onError(new RuntimeException("Link is not found"));
			return;
		}
		ChannelFuture future = link.getChannel().writeAndFlush(RtcpParser.encode(message));

		if (future.syncUninterruptibly().isSuccess()) callback.onSuccess();
		else System.out.println(future.cause());
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
