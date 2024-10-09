package edu.netty.server.processor;

import edu.netty.common.ServerChannelUtils;
import edu.netty.server.channel.MessageChannel;
import edu.netty.server.channel.transports.DatagramChannelInitializer;
import edu.netty.server.channel.transports.DatagramMessageChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class DatagramMessageProcessor extends MessageProcessor {
    private final int threads;

    protected ArrayList<Channel> serverChannels;
    private final EventLoopGroup eventLoopGroup;

    public DatagramMessageProcessor(int port) {
        this.port = port;
        this.threads = Runtime.getRuntime().availableProcessors();

        this.serverChannels = new ArrayList<>();
        this.eventLoopGroup = ServerChannelUtils.createEventLoopGroup();
    }

    @Override
    public MessageChannel createMessageChannel(Channel channel) {
        return new DatagramMessageChannel(this, channel);
    }

    @Override
    public void start() {
        Bootstrap connectionlessBootstrap = new Bootstrap();

        connectionlessBootstrap.group(eventLoopGroup)
            .option(EpollChannelOption.SO_REUSEPORT, true)
            .option(EpollChannelOption.IP_RECVORIGDSTADDR, true)
            .option(EpollChannelOption.IP_FREEBIND, true);

        connectionlessBootstrap
            .channel(ServerChannelUtils.getDatagramChannel())
            .handler(new DatagramChannelInitializer(this));

        ChannelFuture future = connectionlessBootstrap.bind(new InetSocketAddress("127.0.0.1", port));
        future.awaitUninterruptibly();

        for (int i = 0; i < threads; ++i) {
            future = connectionlessBootstrap.bind(new InetSocketAddress("0.0.0.0", port));

            future.awaitUninterruptibly();

            if (future.isSuccess()) {
                System.out.println("[UDP-PROCESSOR] Channel started on port " + port);
            } else {
                System.out.println("[UDP-PROCESSOR] Channel not connected: " + future.cause());
            }

            serverChannels.add(future.channel());
        }
    }
}
