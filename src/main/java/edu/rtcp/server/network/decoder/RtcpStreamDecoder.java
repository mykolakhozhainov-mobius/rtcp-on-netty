package edu.rtcp.server.network.decoder;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.parser.RtcpParser;
import edu.rtcp.server.executor.MessageExecutor;
import edu.rtcp.server.executor.tasks.MessageProcessingTask;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RtcpStreamDecoder extends ByteToMessageDecoder {
	public static Logger logger = LogManager.getLogger(RtcpStreamDecoder.class);

	private final RtcpStack stack;
	private final MessageExecutor executor;

	public RtcpStreamDecoder(RtcpStack stack) {
		this.stack = stack;
		this.executor = stack.getMessageExecutor();
	}
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    	
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        
        if (in.readableBytes() < 8) {
            return;
        }

        ByteBuf copied = in.copy();
        if (in.readableBytes() < copied.readInt() + 4) {
            return;
        }

        copied.release();
        in.readInt();
        RtcpBasePacket message = (RtcpBasePacket) RtcpParser.decode(in);
        
		if (this.stack.isLogging) {
            logger.info("{} from session {}", message.getHeader().getPacketType(), message.getSSRC());
		}
		
		this.executor.addTaskLast(new MessageProcessingTask(message, socketAddress, this.stack));
    }
    
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (stack.transport == TransportEnum.TCP) {
			ctx.channel().close();
		}

		logger.error(cause.getMessage(), cause);
	}
}
