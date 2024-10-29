package edu.rtcp.server.network.channel;

import edu.rtcp.RtcpStack;
import edu.rtcp.server.network.decoder.RtcpStreamDecoder;
import edu.rtcp.server.network.encoder.RtcpMessageEncoder;
import edu.rtcp.server.network.handler.MessageHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class StreamChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final RtcpStack stack;

    public StreamChannelInitializer(RtcpStack stack) {
        this.stack = stack;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Encoder
        pipeline.addLast("encoder", new RtcpMessageEncoder());

        // Decoder
        pipeline.addLast("decoder", new RtcpStreamDecoder());

        // Message handler
        pipeline.addLast("messageHandler", new MessageHandler(stack));
    }
}