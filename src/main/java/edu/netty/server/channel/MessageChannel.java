package edu.netty.server.channel;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import edu.netty.server.handlers.MessageHandler;
import edu.netty.server.MessageProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocket13FrameEncoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;

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
    public void process(Message message) {
        UUID sessionId = message.sessionId;
        MessageTypeEnum type = message.type;

        if (type == MessageTypeEnum.OPEN) {
            this.messageProcessor.createSession(sessionId);
        }

        if (this.messageProcessor.isSessioned(sessionId)) {
            this.writeMessage(new Message(
                    sessionId,
                    MessageTypeEnum.ACK,
                    message.content
            ).toByteBuf());
        }

        System.out.println("[CHANNEL] Channel " + channel.id() + " proceeded message");
    }

    public void writeMessage(ByteBuf message) {
        ChannelFuture future = channel.writeAndFlush(message);

        final ChannelFuture finalFuture = future;

        future.addListener((ChannelFutureListener) completeFuture -> {
            assert finalFuture == completeFuture;
            if (!finalFuture.isSuccess()) {
                System.out.println(
                        "[CHANNEL] Response message not sent successfully " + finalFuture.cause().getMessage()
                );
            } else {
                System.out.println("[CHANNEL] Response message sent successfully");
            }
        });
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

    @Override
    public boolean isSessioned(Message message) {
        return this.messageProcessor.isSessioned(message.sessionId);
    }
}
