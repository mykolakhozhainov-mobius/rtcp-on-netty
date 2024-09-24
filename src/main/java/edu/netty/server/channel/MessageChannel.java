package edu.netty.server.channel;

import edu.netty.common.SimpleMessage;
import edu.netty.server.handlers.MessageHandler;
import edu.netty.server.MessageProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocket13FrameEncoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class MessageChannel implements ProcessingChannel {
    private MessageProcessor messageProcessor;
    private final Bootstrap bootstrap;

    private Channel channel;

    private final InetAddress address;
    private final int port;

    public MessageChannel(
            MessageProcessor messageProcessor,
            Channel channel) {
        this(
                ((InetSocketAddress) channel.remoteAddress()).getAddress(),
                ((InetSocketAddress) channel.remoteAddress()).getPort(),
                messageProcessor
        );

        this.channel = channel;
        this.messageProcessor = messageProcessor;
    }

    public MessageChannel(InetAddress inetAddress, int port, MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;

        bootstrap = new Bootstrap();
        io.netty.channel.ChannelInitializer<SocketChannel> nettyChannelInitializer = new MessageChannelInitializer(messageProcessor);

        bootstrap.group(messageProcessor.workerGroup)
                .channel(messageProcessor.getEpollServerSocketChannel())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(nettyChannelInitializer)
                .handler(new io.netty.channel.ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                       ChannelPipeline p = ch.pipeline();
                            // Encoder
                            p.addLast(new WebSocket13FrameEncoder(false));

                            p.addLast(new MessageHandler(messageProcessor));
                        }
                    });

        this.address = inetAddress;
        this.port = port;
    }

    public static String getKey(InetAddress inetAddr, int port) {
        return (inetAddr.getHostAddress() + ":" + port).toLowerCase();
    }

    public String getKey() {
        return MessageChannel.getKey(address, port);
    }

    @Override
    public void process(SimpleMessage message) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[CHANNEL] Channel " + channel.id() + " proceeded message");
    }

    public void close(boolean removeSocket) {

        if (channel != null && channel.isActive()) {
            channel.close();
        }

        if (removeSocket) { messageProcessor.remove(this); }
    }

    public Channel getChannel() {
        return channel;
    }
}
