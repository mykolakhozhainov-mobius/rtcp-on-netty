package edu.rtcp.server.network.processor.transport;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.decoder.StreamMessageDecoder;
import edu.rtcp.server.network.handler.MessageHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class StreamChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final RtcpStack stack;

    public StreamChannelInitializer(RtcpStack stack) {
        this.stack = stack;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Decoder
        pipeline.addLast("decoder", new StreamMessageDecoder());

        // Encoder
        pipeline.addLast("byteEncoder", new ByteArrayEncoder());

        // Message handler
        pipeline.addLast("messageHandler", new MessageHandler(stack));
    }
}