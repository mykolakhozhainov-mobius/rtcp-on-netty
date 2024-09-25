package edu.netty.client;

import edu.netty.common.MessageDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class MobiusClientInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new MessageDecoder());
		pipeline.addLast(new StringEncoder(), new MobiusClientHandler());
	}
}
