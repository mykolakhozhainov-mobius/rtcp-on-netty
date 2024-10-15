package edu.rtcp.common;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerChannelUtils {
    private static final boolean isEpoll = Epoll.isAvailable();

    public static Class<? extends ServerSocketChannel> getSocketChannel() {
        return isEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends DatagramChannel> getDatagramChannel() {
        return isEpoll? EpollDatagramChannel.class : NioDatagramChannel.class;
    }

    public static EventLoopGroup createEventLoopGroup() {
        return isEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }
}
