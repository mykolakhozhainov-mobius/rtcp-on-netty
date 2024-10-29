package edu.rtcp.server.network.decoder;

import edu.rtcp.common.message.rtcp.parser.RtcpParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class RtcpDatagramDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
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
        out.add(RtcpParser.decode(in));
    }
}
