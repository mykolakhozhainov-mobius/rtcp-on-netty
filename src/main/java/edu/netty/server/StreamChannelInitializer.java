package edu.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class StreamChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final MessageProcessor messageProcessor;

    public StreamChannelInitializer(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Encoder
        pipeline.addLast("byteArrayEncoder", new ByteArrayEncoder());

        // Message handler
        pipeline.addLast("messageHandler", new MessageHandler(messageProcessor));
    }
}