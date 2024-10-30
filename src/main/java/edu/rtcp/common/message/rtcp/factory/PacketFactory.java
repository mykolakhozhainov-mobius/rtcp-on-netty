package edu.rtcp.common.message.rtcp.factory;

import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.packet.*;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.common.message.rtcp.parts.chunk.Chunk;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Random;

public class PacketFactory {
    private static final byte VERSION = 1;
    private static final boolean IS_PADDING = false;
    private static final short PACKET_LENGTH = Short.MAX_VALUE;

    private static final Random random = new Random();

    private RtcpHeader createHeader(byte itemCount, PacketTypeEnum packetType) {
        return new RtcpHeader(
                VERSION,
                IS_PADDING,
                itemCount,
                packetType,
                PACKET_LENGTH
        );
    }

    public ApplicationDefined createApplicationDefined(
            byte itemCount, // if item count == 0 -> this is ACK message
            int ssrc,
            String name, // max length = 4 bytes (4 symbols)
            int applicationDependentData
    ) {
        return new ApplicationDefined(
                this.createHeader(itemCount, PacketTypeEnum.APP),
                ssrc,
                name,
                applicationDependentData
        );
    }

    public Bye createBye(
            byte itemCount, // if item count == 0 -> this is ACK message
            int ssrc,
            String reason
    ) {
        Bye byePacket = new Bye(
                this.createHeader(itemCount, PacketTypeEnum.BYE),
                ssrc
        );
        
        if (!reason.isEmpty()) {
            byePacket.setLengthOfReason(reason.length());
            byePacket.setReason(reason);
        }
        
        return byePacket;
    }

    public ReceiverReport createReceiverReport(
            byte itemCount, // if item count == 0 -> this is ACK message
            int ssrc,
            List<ReportBlock> reportBlocks
    ) {
        ReceiverReport rrPacket = new ReceiverReport(
                this.createHeader(itemCount, PacketTypeEnum.RECEIVER_REPORT),
                ssrc
        );
        
        if (reportBlocks != null && !reportBlocks.isEmpty()) {
            rrPacket.setReportBlocks(reportBlocks);
        }
       
	
        return rrPacket;
    }
   
    public SenderReport createSenderReport(
            byte itemCount, // if item count == 0 -> this is ACK message
            int ssrc,
            List<ReportBlock> reportBlocks,
            ByteBuf profileSpecificExtensions
    ) {
        SenderReport srPacket = new SenderReport(
                this.createHeader(itemCount, PacketTypeEnum.SENDER_REPORT),
                ssrc,
                random.nextInt(100),
                random.nextInt(100),
                random.nextInt(100),
                random.nextInt(10),
                random.nextInt(10)
        );
        
        if (reportBlocks != null && !reportBlocks.isEmpty()) {
            srPacket.setReportBlocks(reportBlocks);
        }
        
        if (profileSpecificExtensions != null) {
            srPacket.setProfileSpecificExtensions(profileSpecificExtensions);
        }
        
        return srPacket;
    }

  
    public SourceDescription createSourceDescription(
            byte itemCount, // if item count == 0 -> this is ACK message
            short length,
            int ssrc,
            List<Chunk> chunks
    ) {
        RtcpHeader header = new RtcpHeader(VERSION, IS_PADDING, itemCount, PacketTypeEnum.SOURCE_DESCRIPTION, length);
        
        SourceDescription sdPacket = new SourceDescription(header);

        if (chunks != null && !chunks.isEmpty()) {
            sdPacket.setChunks(chunks);
        }
        
        return sdPacket;
    }

    public ReportBlock createReportBlock(int ssrc, byte fractionLost) {
        return new ReportBlock(
                ssrc,
                fractionLost,
                random.nextInt(),
                random.nextInt(),
                random.nextInt(),
                random.nextInt(),
                random.nextInt()
        );
    }
}
