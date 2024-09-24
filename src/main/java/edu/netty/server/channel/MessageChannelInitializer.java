package edu.netty.server.channel;

import edu.netty.common.MessageDecoder;
import edu.netty.server.handlers.MessageHandler;
import edu.netty.server.MessageProcessor;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class MessageChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final MessageProcessor messageProcessor;

    public MessageChannelInitializer(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Decoder
        pipeline.addLast("simpleMessageDecoder", new MessageDecoder());

        // Encoder
        pipeline.addLast("byteArrayEncoder", new ByteArrayEncoder());

        // Message handler
        pipeline.addLast("messageHandler", new MessageHandler(messageProcessor));
    }
}