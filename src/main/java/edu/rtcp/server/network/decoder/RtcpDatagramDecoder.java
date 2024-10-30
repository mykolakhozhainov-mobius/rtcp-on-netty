package edu.rtcp.server.network.decoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.parser.RtcpParser;
import edu.rtcp.server.executor.MessageExecutor;
import edu.rtcp.server.executor.tasks.MessageProcessingTask;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class RtcpDatagramDecoder extends SimpleChannelInboundHandler<DatagramPacket> {
	public static Logger logger = LogManager.getLogger(RtcpDatagramDecoder.class);

	private final RtcpStack stack;
	private final MessageExecutor executor;

	public RtcpDatagramDecoder(RtcpStack stack) {
		this.stack = stack;
		this.executor = stack.getMessageExecutor();
	}
	
	@Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {

        ByteBuf in = msg.content();

        if (in.readableBytes() < 8) {
            return;
        }

        ByteBuf copied = in.copy();
        if (in.readableBytes() < copied.readInt() + 4) {
            return;
        }

        copied.release();

        in.readInt();
        RtcpBasePacket basePacket = RtcpParser.decode(in);
        
        if (this.stack.isLogging) {
            logger.info("{} from session {}", basePacket.getHeader().getPacketType(), basePacket.getSSRC());
		}
		
		this.executor.addTaskLast(new MessageProcessingTask(basePacket, msg.sender(), this.stack));
    }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (stack.transport == TransportEnum.TCP) {
			ctx.channel().close();
		}

		logger.error(cause.getMessage(), cause);
	}
}
