package edu.netty.common.message.rtcp.factory;

import edu.netty.common.message.rtcp.header.RtcpHeader;
import edu.netty.common.message.rtcp.packet.*;
import edu.netty.common.message.rtcp.parts.ReportBlock;
import edu.netty.common.message.rtcp.parts.chunk.Chunk;
import edu.netty.common.message.rtcp.types.PacketTypeEnum;
import io.netty.buffer.ByteBuf;

import java.util.List;

public class PacketFactory 
{

    public ApplicationDefined createApplicationDefined(Byte version, Boolean isPadding, Byte itemCount, PacketTypeEnum packetType, Short length, Integer ssrc, String name, Integer applicationDependentData) 
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length);
        
        ApplicationDefined appPacket = new ApplicationDefined(header, ssrc, name, applicationDependentData);
        
        return appPacket;
    }

    
    public Bye createBye(Byte version, Boolean isPadding, Byte itemCount, PacketTypeEnum packetType, Short length, Integer ssrc, Integer lengthOfReason, String reason) 
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length);
        
        Bye byePacket = new Bye(header, ssrc);
        
        if (lengthOfReason != null && reason != null && !reason.isEmpty()) 
        {
            byePacket.setLengthOfReason(lengthOfReason);
            byePacket.setReason(reason);
        }
        
        return byePacket;
    }

  
    public ReceiverReport createReceiverReport(Byte version, Boolean isPadding, Byte itemCount, PacketTypeEnum packetType, Short length, Integer ssrc,List<ReportBlock> reportBlocks) 
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length);
        
        ReceiverReport rrPacket = new ReceiverReport(header, ssrc);
        
        if (reportBlocks != null && !reportBlocks.isEmpty()) 
        {
            rrPacket.setReportBlocks(reportBlocks);
        }
       
	
        return rrPacket;
        
    }

   
    public SenderReport createSenderReport(Byte version, Boolean isPadding, Byte itemCount, PacketTypeEnum packetType, Short length, Integer ssrc, Integer ntpTimestampMostSignificant,Integer ntpTimestampLeastSignificant, Integer rtpTimestamp,Integer senderPacketCount, Integer senderOctetCount,List<ReportBlock> reportBlocks,ByteBuf profileSpecificExtensions)
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length);
        
        SenderReport srPacket = new SenderReport(header,ssrc, ntpTimestampMostSignificant, ntpTimestampLeastSignificant, rtpTimestamp, senderPacketCount, senderOctetCount);
        
        if (reportBlocks != null && !reportBlocks.isEmpty()) 
        {
            srPacket.setReportBlocks(reportBlocks);
        }
        
        if (profileSpecificExtensions != null) 
        {
            srPacket.setProfileSpecificExtensions(profileSpecificExtensions);
        }
        
        return srPacket;
    }

  
    public SourceDescription createSourceDescription(Byte version, Boolean isPadding, Byte itemCount, PacketTypeEnum packetType, Short length, Integer ssrc, List<Chunk> chunks) 
    {
        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetType, length);
        
        SourceDescription sdPacket = new SourceDescription(header);
        
    
        if (chunks != null && !chunks.isEmpty()) 
        {
            sdPacket.setChunks(chunks);
        }
        
        return sdPacket;
    }
}
