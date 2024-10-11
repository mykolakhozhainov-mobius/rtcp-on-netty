package edu.netty.client;

import edu.netty.client.handlers.DatagramClientInitializer;
import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.UUID;

public class DatagramClient extends AbstractClient {
    public ChannelFuture future;
    private static final InetSocketAddress serverAddress = new InetSocketAddress("localhost", 5060);

    private void sendMessage(Message message) {
        DatagramPacket packet = new DatagramPacket(message.toByteBuf(), serverAddress);

        future.channel().writeAndFlush(packet).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("Datagram sent to the server.");
            } else {
                System.err.println("Failed to send datagram: " + channelFuture.cause());
            }
        });
    }

    @Override
    public void start() {
        this.executor.start(64, 10);

        EventLoopGroup group = new EpollEventLoopGroup(); // Change to EpollEventLoopGroup
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(EpollDatagramChannel.class) // Use EpollDatagramChannel
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) {
                            channel = ch;
                            ch.pipeline().addLast(new DatagramClientInitializer(sessions));
                        }
                    });

            this.future = bootstrap.bind(0).sync();

            final int SESSIONS = 3;
            final int MESSAGES = 10000;

            for (int i = 0; i < SESSIONS; i++) { this.createSession(UUID.randomUUID()); }

            this.sessions.values().forEach(session -> {
                session.addMessageTask(
                        new Message(session.id, MessageTypeEnum.OPEN, "Open new session " + session.id),
                        (callSession, message) -> sendMessage(message)
                );

                for (int i = 0; i < MESSAGES; i++) {
                    session.addMessageTask(
                            new Message(session.id, MessageTypeEnum.DATA, "Message from client #" + i),
                            (callSession, message) -> sendMessage(message)
                    );
                }
            });

            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        DatagramClient client = new DatagramClient();
        client.start();
    }
}
