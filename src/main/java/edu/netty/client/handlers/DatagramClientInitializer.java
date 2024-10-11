package edu.netty.client.handlers;

import java.util.Map;
import java.util.UUID;

import edu.netty.common.decoder.DatagramMessageDecoder;
import edu.netty.common.session.Session;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.string.StringEncoder;

public class DatagramClientInitializer extends ChannelInitializer<DatagramChannel> {
    public final Map<UUID, Session> sessions;

    public DatagramClientInitializer(Map<UUID, Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    protected void initChannel(DatagramChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new DatagramMessageDecoder());

        pipeline.addLast(new StringEncoder());

        pipeline.addLast(new ClientHandler(sessions));
    }
}
