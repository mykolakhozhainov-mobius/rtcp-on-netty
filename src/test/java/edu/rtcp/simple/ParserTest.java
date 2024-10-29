package edu.rtcp.simple;

import edu.rtcp.common.message.rtcp.factory.PacketFactory;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.packet.*;
import edu.rtcp.common.message.rtcp.parser.RtcpDecoder;
import edu.rtcp.common.message.rtcp.parser.RtcpEncoder;
import io.netty.buffer.ByteBuf;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest {
    private final static PacketFactory packetFactory = new PacketFactory();

    private final static SenderReport SENDER_REPORT = packetFactory.createSenderReport(
            (byte) 0,
            1,
            null,
            null
    );
    private final static ReceiverReport RECEIVER_REPORT = packetFactory.createReceiverReport(
            (byte) 0,
            2,
            null
    );
    private final static ApplicationDefined APPLICATION_DEFINED = packetFactory.createApplicationDefined(
            (byte) 0,
            3,
            "Some",
            0
    );
    private final static SourceDescription SOURCE_DESCRIPTION = packetFactory.createSourceDescription(
            (byte) 0,
            (short) 0,
            4,
            null
    );
    private final static Bye BYE = packetFactory.createBye(
            (byte) 0,
            5,
            "Some reason"
    );

    private static final int HEADER_LENGTH = 4;

    @Test
    public void testHeader() {
        for (RtcpBasePacket packet : new RtcpBasePacket[] {
                SENDER_REPORT,
                RECEIVER_REPORT,
                SOURCE_DESCRIPTION,
                BYE,
                APPLICATION_DEFINED
        }) {
            // Test Case 1: is header length is fixed (16 bytes)
            ByteBuf encoded = RtcpEncoder.encodeHeader(packet.getHeader());
            assertEquals(HEADER_LENGTH, encoded.readableBytes());

            // Test Case 2: are header params are equal after encoding and decoding
            RtcpHeader decoded = RtcpDecoder.decodeHeader(encoded);
            assertEquals(packet.getHeader().getItemCount(), decoded.getItemCount());
            assertEquals(packet.getHeader().getPacketType(), decoded.getPacketType());
            assertEquals(packet.getHeader().getLength(), decoded.getLength());
            assertEquals(packet.getHeader().getIsPadding(), decoded.getIsPadding());
            assertEquals(packet.getHeader().getVersion(), decoded.getVersion());
        }
    }

    @Test
    public void testApplicationDefined() {
        // Test Case 1: testing APP message length
        ByteBuf encodedApp = RtcpEncoder.encodeApp(APPLICATION_DEFINED);
        assertEquals(16, encodedApp.readableBytes());

        // Test Case 2: is APP packet params are the same
        ApplicationDefined decodedApp = RtcpDecoder.decodeApp(encodedApp);
        assertEquals(APPLICATION_DEFINED.getName(), decodedApp.getName());
        assertEquals(APPLICATION_DEFINED.getApplicationDependentData(), decodedApp.getApplicationDependentData());
    }

    @Test
    public void testSourceDescription() {
        ByteBuf encodedSD = RtcpEncoder.encodeSourceDescription(SOURCE_DESCRIPTION);

        // Test Case 1: are SR packet params are the same
        SourceDescription decodedSR = RtcpDecoder.decodeSourceDescription(encodedSD);
        assertEquals(SOURCE_DESCRIPTION.getChunks(), decodedSR.getChunks());
    }

    @Test
    public void testSenderReport() {
        // Test Case 1: testing SR message length
        ByteBuf encodedSR = RtcpEncoder.encodeSenderReport(SENDER_REPORT);
        assertEquals(28, encodedSR.readableBytes());

        // Test Case 2: is SR packet params are the same
        SenderReport decodedSR = RtcpDecoder.decodeSenderReport(encodedSR);
        assertEquals(SENDER_REPORT.getReportBlocks(), decodedSR.getReportBlocks());
        assertEquals(SENDER_REPORT.getSenderOctetCount(), decodedSR.getSenderOctetCount());
        assertEquals(SENDER_REPORT.getSenderPacketCount(), decodedSR.getSenderPacketCount());
        assertEquals(SENDER_REPORT.getNtpTimestampLeastSignificant(), decodedSR.getNtpTimestampLeastSignificant());
        assertEquals(SENDER_REPORT.getNtpTimestampMostSignificant(), decodedSR.getNtpTimestampMostSignificant());
        assertEquals(SENDER_REPORT.getRtpTimestamp(), decodedSR.getRtpTimestamp());
    }

    @Test
    public void testReceiverReport() {
        // Test Case 1: testing RR message length
        ByteBuf encodedRR = RtcpEncoder.encodeReceiverReport(RECEIVER_REPORT);
        assertEquals(8, encodedRR.readableBytes());

        // Test Case 2: is RR packet params are the same
        ReceiverReport decodedRR = RtcpDecoder.decodeReceiverReport(encodedRR);
        assertEquals(RECEIVER_REPORT.getReportBlocks(), decodedRR.getReportBlocks());
    }

    @Test
    public void testBye() {
        // Test Case 1: testing BYE message length
        ByteBuf encodedBye = RtcpEncoder.encodeBye(BYE);

        // Test Case 2: is BYE packet params are the same
        Bye decodeBye = RtcpDecoder.decodeBye(encodedBye);
        assertEquals(BYE.getReason(), decodeBye.getReason());
        assertEquals(BYE.getLengthOfReason(), decodeBye.getLengthOfReason());
    }
}
