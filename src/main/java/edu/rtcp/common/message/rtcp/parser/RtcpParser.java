package edu.rtcp.common.message.rtcp.parser;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.*;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class RtcpParser {
    private static PacketTypeEnum decodePacketType(ByteBuf message) {
        ByteBuf working = message.copy();

        working.skipBytes(1);
        int typeInInt = ((int) working.readByte()) & 0xff;

        return PacketTypeEnum.fromInt(typeInInt);
    }

    public static RtcpBasePacket decode(ByteBuf message) {
        final RtcpDecoder decoder = new RtcpDecoder();
        PacketTypeEnum type = decodePacketType(message);

        switch (type) {
            case SENDER_REPORT:
                return decoder.decodeSenderReport(message);
            case RECEIVER_REPORT:
                return decoder.decodeReceiverReport(message);
            case APP:
                return decoder.decodeApp(message);
            case BYE:
                return decoder.decodeBye(message);
            case SOURCE_DESCRIPTION:
                return decoder.decodeSourceDescription(message);
        }

        return null;
    }

    public static ByteBuf encode(RtcpBasePacket packet) {
        final RtcpEncoder encoder = new RtcpEncoder();
        PacketTypeEnum type = packet.getHeader().getPacketType();

        ByteBuf encoded;

        switch (type) {
            case SENDER_REPORT:
                encoded = encoder.encodeSenderReport((SenderReport) packet);
                break;
            case RECEIVER_REPORT:
                encoded = encoder.encodeReceiverReport((ReceiverReport) packet);
                break;
            case APP:
                encoded = encoder.encodeApp((ApplicationDefined) packet);
                break;
            case BYE:
                encoded = encoder.encodeBye((Bye) packet);
                break;
            case SOURCE_DESCRIPTION:
                encoded = encoder.encodeSourceDescription((SourceDescription) packet);
                break;
            default:
                encoded = Unpooled.buffer();
                break;
        }

        ByteBuf result = Unpooled.buffer();
        result.writeInt(encoded.readableBytes());
        result.writeBytes(encoded);

        return result;
    }
}