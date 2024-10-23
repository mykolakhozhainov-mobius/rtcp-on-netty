package edu.rtcp.server.network.processor.transport;

import edu.rtcp.common.ServerChannelUtils;
import edu.rtcp.RtcpStack;
import edu.rtcp.server.network.processor.AbstractProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class DatagramProcessor extends AbstractProcessor {
    private final int threads;

    protected ArrayList<Channel> serverChannels;
    private final EventLoopGroup eventLoopGroup;

    public DatagramProcessor(int port, RtcpStack stack) {
        this.port = port;
        this.stack = stack;
        this.threads = Runtime.getRuntime().availableProcessors();

        this.serverChannels = new ArrayList<>();
        this.eventLoopGroup = ServerChannelUtils.createEventLoopGroup();
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
            .handler(new DatagramChannelInitializer(this.stack));

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
