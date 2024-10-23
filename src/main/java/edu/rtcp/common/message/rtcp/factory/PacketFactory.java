package edu.rtcp.common.message.rtcp.factory;

import java.util.List;

import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.packet.ApplicationDefined;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.common.message.rtcp.packet.SourceDescription;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.common.message.rtcp.parts.chunk.Chunk;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;

public class PacketFactory {

   
    public ApplicationDefined createApplicationDefined(Short version, Boolean isPadding, Short itemCount, PacketTypeEnum packetType,Integer length, Integer ssrc, String name, Long applicationDependentData) 
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length, ssrc);
        ApplicationDefined appPacket = new ApplicationDefined(header, name, applicationDependentData);
        
        return appPacket;
    }

    
    public Bye createBye(Short version, Boolean isPadding, Short itemCount, PacketTypeEnum packetType,Integer length, Integer ssrc, String reason) 
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length, ssrc);
        Bye byePacket = new Bye(header, reason);
        
        return byePacket;
    }

  
    public ReceiverReport createReceiverReport(Short version, Boolean isPadding, Short itemCount, PacketTypeEnum packetType,Integer length, Integer ssrc,List<ReportBlock> reportBlocks) 
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length, ssrc);
        ReceiverReport rrPacket = new ReceiverReport(header);
        
        if (reportBlocks != null && !reportBlocks.isEmpty()) 
        {
            rrPacket.setReportBlocks(reportBlocks);
        }
       
	
        return rrPacket;
        
    }

   
    public SenderReport createSenderReport(Short version, Boolean isPadding, Short itemCount, PacketTypeEnum packetType,Integer length, Integer ssrc, Integer ntpTimestampMostSignificant,Integer ntpTimestampLeastSignificant, Integer rtpTimestamp,Integer senderPacketCount, Integer senderOctetCount,List<ReportBlock> reportBlocks)
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length, ssrc);
        SenderReport srPacket = new SenderReport(header, ntpTimestampMostSignificant, ntpTimestampLeastSignificant, rtpTimestamp, senderPacketCount, senderOctetCount);
        
        if (reportBlocks != null && !reportBlocks.isEmpty()) 
        {
            srPacket.setReportBlocks(reportBlocks);
        }
        
        return srPacket;
    }

  
    public SourceDescription createSourceDescription(Short version, Boolean isPadding, Short itemCount, PacketTypeEnum packetType,Integer length, Integer ssrc, List<Chunk> chunks) 
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length, ssrc);
        SourceDescription sdPacket = new SourceDescription(header);
        
    
        if (chunks != null && !chunks.isEmpty()) 
        {
            sdPacket.setChunks(chunks);
        }
        
        return sdPacket;
    }
}
