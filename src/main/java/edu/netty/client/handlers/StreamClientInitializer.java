package edu.netty.client.handlers;

import java.util.Map;
import java.util.UUID;

import edu.netty.common.decoder.StreamMessageDecoder;
import edu.netty.common.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.string.StringEncoder;

public class StreamClientInitializer extends ChannelInitializer<Channel> {
    public final Map<UUID, Session> sessions;
    
    public StreamClientInitializer(Map<UUID, Session> sessions) {
    	this.sessions = sessions;
    }

	@Override
	protected void initChannel(Channel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new StreamMessageDecoder());
		pipeline.addLast(new StringEncoder(), new ClientHandler(sessions));
	}
}
