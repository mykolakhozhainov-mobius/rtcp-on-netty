package edu.rtcp.server.network.channel;

import edu.rtcp.RtcpStack;
import edu.rtcp.server.network.decoder.RtcpDatagramDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class DatagramChannelInitializer extends ChannelInitializer<DatagramChannel> {

    private final RtcpStack stack;

    public DatagramChannelInitializer(RtcpStack stack) {
        this.stack = stack;
    }

    @Override
    public void initChannel(DatagramChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Decoder
        pipeline.addLast("decoder", new RtcpDatagramDecoder(this.stack));

        // Encoder
        pipeline.addLast("byteEncoder", new ByteArrayEncoder());
    }
}
