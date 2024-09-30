package edu.netty.client;

import java.util.Map;
import java.util.UUID;

import edu.netty.common.MessageDecoder;
import edu.netty.common.session.Session;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class MobiusClientInitializer extends ChannelInitializer<SocketChannel> {
    public final Map<UUID, Session> sessions;
    
    public MobiusClientInitializer (Map<UUID, Session> sessions) {
    	this.sessions = sessions;
    }

	@Override
	protected void initChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new MessageDecoder());
		pipeline.addLast(new StringEncoder(), new MobiusClientHandler(sessions));
	}
}
