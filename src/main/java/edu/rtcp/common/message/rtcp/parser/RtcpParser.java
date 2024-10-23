package edu.rtcp.common.message.rtcp.parser;

import edu.rtcp.common.message.rtcp.exception.RtcpException;
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

        switch (type) {
            case SENDER_REPORT:
                return encoder.encodeSenderReport((SenderReport) packet);
            case RECEIVER_REPORT:
                return encoder.encodeReceiverReport((ReceiverReport) packet);
            case APP:
                return encoder.encodeApp((ApplicationDefined) packet);
            case BYE:
                return encoder.encodeBye((Bye) packet);
            case SOURCE_DESCRIPTION:
                return encoder.encodeSourceDescription((SourceDescription) packet);
        }

        return Unpooled.buffer();
    }
}