package edu.netty.server.channel.transports;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import edu.netty.server.channel.AbstractChannel;
import edu.netty.server.processor.StreamMessageProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;

public class StreamMessageChannel extends AbstractChannel {
    private final InetAddress address;
    private final int port;

    public StreamMessageChannel(
            StreamMessageProcessor streamMessageProcessor,
            Channel channel) {
        this(
                ((InetSocketAddress) channel.remoteAddress()).getAddress(),
                ((InetSocketAddress) channel.remoteAddress()).getPort(),
                streamMessageProcessor
        );

        this.channel = channel;
        this.messageProcessor = streamMessageProcessor;
    }

    public StreamMessageChannel(InetAddress inetAddress, int port, StreamMessageProcessor streamMessageProcessor) {
        this.messageProcessor = streamMessageProcessor;

        this.address = inetAddress;
        this.port = port;
    }

    public String getKey() {
        return StreamMessageChannel.getKey(address, port);
    }

    @Override
    public void process(Message message) {
        UUID sessionId = message.sessionId;
        MessageTypeEnum type = message.type;

        if (type == MessageTypeEnum.OPEN) {
            this.messageProcessor.createSession(sessionId);
        }

        if (this.messageProcessor.isSessioned(sessionId)) {
            this.sendMessage(new Message(
                    sessionId,
                    MessageTypeEnum.ACK,
                    message.content
            ).toByteBuf());
        }

        System.out.println("[CHANNEL] Channel " + channel.id() + " proceeded message");
    }

    @Override
    public void sendMessage(ByteBuf message) {
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
}
