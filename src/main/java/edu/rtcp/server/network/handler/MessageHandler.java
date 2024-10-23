package edu.rtcp.server.network.handler;

import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.RtcpStack;
import edu.rtcp.server.executor.MessageExecutor;
import edu.rtcp.server.executor.tasks.MessageProcessingTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;

public class MessageHandler extends ChannelInboundHandlerAdapter {
	private final RtcpStack stack;
	private final MessageExecutor executor;

	public MessageHandler(RtcpStack stack) {
		this.stack = stack;
		this.executor = stack.getMessageExecutor();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		RtcpBasePacket message = (RtcpBasePacket) msg;

		System.out.println("[HANDLER] New message content from " + ctx.channel() + ":");
		System.out.println(message);

		this.executor.addTaskLast(new MessageProcessingTask(message, this.stack));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (cause instanceof ReadTimeoutException) {
			System.out.println("[HANDLER] Read Timeout Received on channel " + ctx.channel() + ", closing channel");
		} else {
			System.out.println("[HANDLER] " + cause.toString());
			System.out.println("[HANDLER] Exception " + cause.getClass().getName() + " on channel " + stack.isServer
					+ ctx.channel() + ", closing channel handle context");
		}
		if (stack.transport == TransportEnum.TCP) {
			ctx.channel().close();
		}
		System.out.println(cause);
	}
}
