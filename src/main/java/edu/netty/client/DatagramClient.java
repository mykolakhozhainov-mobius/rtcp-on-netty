package edu.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class DatagramClient {

    public static void main(String[] args) {
        EventLoopGroup group = new EpollEventLoopGroup(); // Change to EpollEventLoopGroup
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(EpollDatagramChannel.class) // Use EpollDatagramChannel
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) throws Exception {
                            // You can add handlers here if needed
                        }
                    });

            // Bind to a local port if necessary (optional)
            ChannelFuture future = bootstrap.bind(0).sync();

            // Create the datagram packet
            String message = "Hello, Server!";
            byte[] data = message.getBytes();
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", 5060); // Change "localhost" to the server IP if needed
            DatagramPacket packet = new DatagramPacket(Unpooled.wrappedBuffer(data), serverAddress);

            // Send the datagram packet
            future.channel().writeAndFlush(packet).sync();

            System.out.println("Datagram sent to the server.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
