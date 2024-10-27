package edu.rtcp.server.network.handler;

import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.RtcpStack;
import edu.rtcp.server.executor.MessageExecutor;
import edu.rtcp.server.executor.tasks.MessageProcessingTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageHandler extends ChannelInboundHandlerAdapter {
	public static Logger logger = LogManager.getLogger(MessageHandler.class);

	private final RtcpStack stack;
	private final MessageExecutor executor;

	public MessageHandler(RtcpStack stack) {
		this.stack = stack;
		this.executor = stack.getMessageExecutor();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		RtcpBasePacket message = (RtcpBasePacket) msg;

		if (this.stack.isLogging) {
            logger.info("{} from session {}", message.getHeader().getPacketType(), message.getSSRC());
		}

		this.executor.addTaskLast(new MessageProcessingTask(message, this.stack));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (stack.transport == TransportEnum.TCP) {
			ctx.channel().close();
		}

		logger.error(cause.getMessage(), cause);
	}
}
