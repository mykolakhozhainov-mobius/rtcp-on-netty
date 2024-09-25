package edu.netty.client;

import edu.netty.common.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MobiusClientHandler extends ChannelInboundHandlerAdapter{
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    System.out.println("channelActive");
	    super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    super.channelRead(ctx, msg);

		Message message = (Message) msg;
		System.out.println(message);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    System.out.println("exceptionCaught");
	    super.exceptionCaught(ctx, cause);

	    cause.printStackTrace();
	    ctx.close();

	}

}
