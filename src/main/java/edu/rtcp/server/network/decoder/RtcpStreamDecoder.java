package edu.rtcp.server.network.decoder;

import edu.rtcp.common.message.rtcp.parser.RtcpParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RtcpStreamDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(RtcpParser.decode(in));
    }
}
