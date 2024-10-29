package edu.rtcp.server.network.decoder;

import edu.rtcp.common.message.rtcp.parser.RtcpParser;
import io.netty.buffer.ByteBuf;

import java.util.List;

public class RtcpDecodingUtils {
    public static void handle(ByteBuf in, List<Object> out) {
        while (in.readableBytes() >= 8) {
            ByteBuf copied = in.copy();
            if (in.readableBytes() < copied.readInt() + 4) {
                return;
            }

            copied.release();

            in.readInt();
            out.add(RtcpParser.decode(in));
        }
    }
}
