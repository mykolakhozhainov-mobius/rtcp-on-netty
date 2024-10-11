package edu.netty.server.channel.transports;

import edu.netty.common.decoder.StreamMessageDecoder;
import edu.netty.server.handlers.StreamMessageHandler;
import edu.netty.server.processor.StreamMessageProcessor;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class StreamChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final StreamMessageProcessor streamMessageProcessor;

    public StreamChannelInitializer(StreamMessageProcessor streamMessageProcessor) {
        this.streamMessageProcessor = streamMessageProcessor;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Decoder
        pipeline.addLast("decoder", new StreamMessageDecoder());

        // Encoder
        pipeline.addLast("byteEncoder", new ByteArrayEncoder());

        // Message handler
        pipeline.addLast("messageHandler", new StreamMessageHandler(streamMessageProcessor));
    }
}