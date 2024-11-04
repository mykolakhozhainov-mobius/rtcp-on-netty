package edu.rtcp.server.network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;

public class NetworkManager {
	public static Logger logger = LogManager.getLogger(NetworkManager.class);

	private final RtcpStack stack;

	private final ConcurrentHashMap<String, NetworkLink> links = new ConcurrentHashMap<>();
	private final AtomicInteger linkIndex = new AtomicInteger();

	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;

	private static final int BUFFER_SIZE = 128 * 1024;

	public NetworkManager(RtcpStack stack) {
		this.stack = stack;

		this.bossGroup = ServerChannelUtils.createEventLoopGroup();
		this.workerGroup = ServerChannelUtils.createEventLoopGroup();
	}

	public void addLink(String linkId, InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
		NetworkLink link = stack.isServer ? getLinkByAddress(remoteAddress) : getLinkByAddress(localAddress);
		if (link != null) return;

		this.links.put(linkId, new NetworkLink(linkId, remoteAddress, localAddress));
	}

	public NetworkLink getLinkByAddress(InetSocketAddress address) {
		for (NetworkLink link : links.values()) {
			if (link.getRemoteAddress().equals(address)) {
				return link;
			}
		}

		return null;
	}

	private void startStreamServer(InetSocketAddress localAddress) {
		ServerBootstrap streamBootstrap = new ServerBootstrap()
				.group(bossGroup, workerGroup)
				.channel(ServerChannelUtils.getSocketChannel())
				.option(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
				.option(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
				.childHandler(new ChannelInitializer<SocketChannel>() {
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

		ChannelFuture streamFuture = streamBootstrap
				.bind(localAddress)
				.syncUninterruptibly()
				.awaitUninterruptibly();

		new Thread(() -> {
			try {
				streamFuture.channel().closeFuture().sync();
			} catch (InterruptedException error) {
				logger.error(error);
			} finally {
				this.workerGroup.shutdownGracefully();
				this.bossGroup.shutdownGracefully();
			}
		}).start();

		for (NetworkLink foundLink : links.values()) {
			foundLink.setChannel(streamFuture.channel());
		}
	}

	private void startDatagramServer(InetSocketAddress localAddress) {
		Bootstrap datagramBootstrap = new Bootstrap()
				.channel(ServerChannelUtils.getDatagramChannel())
				.group(workerGroup)
				.option(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
				.option(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
				.option(EpollChannelOption.SO_REUSEPORT, true)
				.option(EpollChannelOption.IP_RECVORIGDSTADDR, true)
				.option(EpollChannelOption.IP_FREEBIND, true)
				.handler(new DatagramChannelInitializer(stack));

		List<ChannelFuture> futures = new ArrayList<ChannelFuture>();
		int maxChannels=stack.getThreadPoolSize();
		if(!Epoll.isAvailable())
			maxChannels=1;
		
		for(int i=0;i<maxChannels;i++)
		{
			ChannelFuture datagramFuture = datagramBootstrap
					.bind(localAddress)
					.syncUninterruptibly()
					.awaitUninterruptibly();
			futures.add(datagramFuture);
		}
		
		int index=0;
		for (NetworkLink foundLink : links.values()) {
			foundLink.setChannel(futures.get(index%futures.size()).channel());
			index++;
		}
	}

	public void startServer(NetworkLink link) {
		if (stack.transport == TransportEnum.TCP) this.startStreamServer(link.getLocalAddress());
		else this.startDatagramServer(link.getLocalAddress());
	}

	private void startStreamClient(NetworkLink link) {
		EventLoopGroup group = ClientChannelUtils.createEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap()
				.group(group)
				.channel(ClientChannelUtils.getSocketChannel())
				.option(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
				.option(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) {
						link.setChannel(socketChannel);
						socketChannel.pipeline().addLast(new StreamChannelInitializer(stack));
					}
				});

		bootstrap
				.connect(link.getRemoteAddress(), link.getLocalAddress())
				.syncUninterruptibly();

		new Thread(() -> {
			try {
				link.getChannel().closeFuture().syncUninterruptibly();
			} finally {
				group.shutdownGracefully();
			}
		}).start();
	}

	private void startDatagramClient(NetworkLink link) {
		EventLoopGroup group = ClientChannelUtils.createEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap()
				.option(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
				.option(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
				.option(EpollChannelOption.IP_RECVORIGDSTADDR, true)
				.option(EpollChannelOption.IP_FREEBIND, true)
				.group(new EpollEventLoopGroup())
				.channel(ClientChannelUtils.getDatagramChannel())
				.handler(new DatagramChannelInitializer(stack));

		link.setChannel(bootstrap.connect(link.getRemoteAddress(), link.getLocalAddress()).channel());

		new Thread(() -> {
			try {
				link.getChannel().closeFuture().sync();
			} catch (InterruptedException error) {
				logger.error(error);
			} finally {
				group.shutdownGracefully();
			}
		}).start();
	}

	public void startClient(NetworkLink link) {
		if (stack.transport == TransportEnum.TCP) this.startStreamClient(link);
		else this.startDatagramClient(link);
	}

	private void startLink(NetworkLink link) {
		if (link == null) return;

		if (this.stack.isServer) this.startServer(link);
		else this.startClient(link);
	}

	public void startAllLinks() {
		Collection<NetworkLink> networkLinks = this.links.values();

		if (this.stack.isServer) {
			this.startLink(networkLinks.iterator().next());
		} else {
			for (NetworkLink link : networkLinks) {
				this.startLink(link);
			}
		}
	}

	public NetworkLink getNextLink() {
		ArrayList<NetworkLink> linksArray = new ArrayList<>(this.links.values());

		return linksArray.get(this.linkIndex.incrementAndGet() % this.links.size());
	}

	public void stopLink(NetworkLink link) {
		link.getChannel().closeFuture();
	}

	public void sendMessage(RtcpBasePacket message, InetSocketAddress address, AsyncCallback callback) {
		ByteBuf encoded = RtcpParser.encode(message);

		NetworkLink link = address == null ? this.getNextLink() : this.getLinkByAddress(address);
		InetSocketAddress resultAddress = address == null ? link.getRemoteAddress() : address;

		link.getChannel().writeAndFlush(
				this.stack.transport == TransportEnum.UDP ?
						new DatagramPacket(encoded, resultAddress) :
						encoded
		);

		callback.onSuccess();
	}

	public void stop() {
		if (links.isEmpty()) return;

		for (NetworkLink link : links.values()) {
			this.stopLink(link);
		}
	}
}