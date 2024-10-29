package edu.rtcp.server.network.channel;

import edu.rtcp.RtcpStack;
import edu.rtcp.server.network.decoder.RtcpDatagramDecoder;
import edu.rtcp.server.network.encoder.RtcpMessageEncoder;
import edu.rtcp.server.network.handler.MessageHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

public class DatagramChannelInitializer extends ChannelInitializer<DatagramChannel> {

    private final RtcpStack stack;

    public DatagramChannelInitializer(RtcpStack stack) {
        this.stack = stack;
    }

    @Override
    public void initChannel(DatagramChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Encoder
        pipeline.addLast("encoder", new RtcpMessageEncoder());

        // Decoder
        pipeline.addLast("decoder", new RtcpDatagramDecoder());

        // Handler
        pipeline.addLast("handler", new MessageHandler(this.stack));
    }
}
