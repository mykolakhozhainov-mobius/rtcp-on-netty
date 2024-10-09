package edu.netty.server.channel.transports;

import edu.netty.common.MessageDecoder;
import edu.netty.server.handlers.DatagramMessageHandler;
import edu.netty.server.processor.DatagramMessageProcessor;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class DatagramChannelInitializer extends ChannelInitializer<DatagramChannel> {

    private final DatagramMessageProcessor datagramMessageProcessor;

    public DatagramChannelInitializer(DatagramMessageProcessor messageProcessor) {
        this.datagramMessageProcessor = messageProcessor;
    }

    @Override
    public void initChannel(DatagramChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Decoder
        pipeline.addLast("decoder", new MessageDecoder());

        // Encoder
        pipeline.addLast("byteEncoder", new ByteArrayEncoder());

        pipeline.addLast("handler", new DatagramMessageHandler(this.datagramMessageProcessor));
    }
}
