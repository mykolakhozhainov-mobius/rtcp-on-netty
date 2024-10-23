package edu.rtcp.server.network.encoder;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.parser.RtcpParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RtcpMessageEncoder extends MessageToByteEncoder<RtcpBasePacket> {
    @Override
    protected void encode(
            ChannelHandlerContext ctx,
            RtcpBasePacket msg,
            ByteBuf out
    ) throws Exception {
        out.writeBytes(RtcpParser.encode(msg));
    }
}
