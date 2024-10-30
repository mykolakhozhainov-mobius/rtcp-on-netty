package edu.rtcp.common;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientChannelUtils {
    private static final boolean isEpoll = Epoll.isAvailable();

    public static Class<? extends SocketChannel> getSocketChannel() {
        return isEpoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends DatagramChannel> getDatagramChannel() {
        return isEpoll? EpollDatagramChannel.class : NioDatagramChannel.class;
    }

    public static EventLoopGroup createEventLoopGroup() {
        return isEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }
}
