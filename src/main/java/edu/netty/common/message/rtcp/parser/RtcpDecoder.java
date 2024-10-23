package edu.netty.common.message.rtcp.parser;

import edu.netty.common.message.rtcp.header.RtcpHeader;
import edu.netty.common.message.rtcp.packet.*;
import edu.netty.common.message.rtcp.parts.ReportBlock;
import edu.netty.common.message.rtcp.parts.chunk.Chunk;
import edu.netty.common.message.rtcp.parts.chunk.SdesItem;
import edu.netty.common.message.rtcp.types.ItemsTypeEnum;
import edu.netty.common.message.rtcp.types.PacketTypeEnum;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
 
public class RtcpDecoder 
{
    public RtcpHeader decodeHeader(ByteBuf headerInBuf) 
    {
       
        byte firstByte = headerInBuf.readByte();
        
        Byte version = (byte) ((firstByte >> 6) & 0x03); 
        Boolean isPadding = (firstByte & 0x20) != 0; 
        Byte itemCount = (byte) (firstByte & 0x1F);
 
        Byte packetTypeValueByte = headerInBuf.readByte();
        PacketTypeEnum packetTypeValue = PacketTypeEnum.fromInt((int) packetTypeValueByte);
 
        Short length = headerInBuf.readShort();
        
        return new RtcpHeader(version, isPadding, itemCount, packetTypeValue, length);
    }
    
    public ApplicationDefined decodeApp(ByteBuf appInBuf)
    {
        RtcpHeader header = decodeHeader(appInBuf);
        int ssrc = appInBuf.readInt();
      
        byte[] nameBytes = new byte[4];
        appInBuf.readBytes(nameBytes); 
             
        String name = new String(nameBytes); 
        
        int applicationDependentData = appInBuf.readInt();
        
        return new ApplicationDefined(header, ssrc, name, applicationDependentData);
    }
    
    public Bye decodeBye(ByteBuf byeInBuf) 
    {
        RtcpHeader header = decodeHeader(byeInBuf);
        int ssrc = byeInBuf.readInt();

        Integer reasonLength = null;
        String reasonString = null;

        if (byeInBuf.isReadable()) 
        {
            reasonLength = byeInBuf.readInt();

            if (reasonLength > 0) {
                byte[] reasonBytes = new byte[reasonLength]; 
                byeInBuf.readBytes(reasonBytes); 
                reasonString = new String(reasonBytes);
            }
        }
        
        Bye bye = new Bye(header, ssrc);
        
        if (reasonLength != null && reasonLength > 0) 
        {
            bye.setLengthOfReason((int) reasonLength);
            bye.setReason(reasonString);
        }

        return bye;
    }
    
    public ReceiverReport decodeReceiverReport(ByteBuf rrBuf) 
    {
        
        RtcpHeader header = decodeHeader(rrBuf);
        
        int ssrc = rrBuf.readInt();
        
        int itemCount = header.getItemCount(); 
        
        ReceiverReport rr = new ReceiverReport(header, ssrc);
        
        if(itemCount>0)
        {
        	
        List<ReportBlock> reportBlocks = new ArrayList<>(itemCount);
        
        
        for (int i = 0; i < itemCount; i++) 
        {
            reportBlocks.add(decodeReportBlock(rrBuf));
            rr.setReportBlocks(reportBlocks);
        }
        
        }
        
        
        return rr;
    }
    
    public SenderReport decodeSenderReport(ByteBuf srBuf) 
    {
        
        RtcpHeader header = decodeHeader(srBuf.readBytes(4));
        
        int ssrc = srBuf.readInt();
        int ntpTimestampMostSignificant = srBuf.readInt();
        int ntpTimestampLeastSignificant = srBuf.readInt();
        int rtpTimestamp = srBuf.readInt();
        int senderPacketCount = srBuf.readInt();
        int senderOctetCount = srBuf.readInt();

        int itemCount = header.getItemCount();
        
        SenderReport sr = new SenderReport(header, ssrc, ntpTimestampMostSignificant, ntpTimestampLeastSignificant, rtpTimestamp, senderPacketCount, senderOctetCount);
        
        if(itemCount>0)
        {
        	
        List<ReportBlock> reportBlocks = new ArrayList<>(itemCount);
        
        for (int i = 0; i < itemCount; i++)
        {
            reportBlocks.add(decodeReportBlock(srBuf.readBytes(24)));
            sr.setReportBlocks(reportBlocks);
        }
        
        }
        
        return sr;
    }
    
    public SourceDescription decodeSourceDescription(ByteBuf sdInBuf) 
    {
        RtcpHeader header = decodeHeader(sdInBuf.readBytes(4));
        
        SourceDescription sd = new SourceDescription(header);
        
        int itemCount = header.getItemCount(); 
        List<Chunk> chunks = new ArrayList<>(itemCount);

        for (int i = 0; i < itemCount; i++) 
        {
            chunks.add(decodeChunk(sdInBuf.readBytes(8))); 
        }
        return sd;
    }
    
    public Chunk decodeChunk(ByteBuf chunkBuf) 
    {
        int ssrc = chunkBuf.readInt();
        
        List<SdesItem> items = new ArrayList<>();

        while (chunkBuf.isReadable()) 
        {
            items.add(decodeSdesItem(chunkBuf)); 
        }

        return new Chunk(ssrc, items);
    }
    
    public SdesItem decodeSdesItem(ByteBuf sdesItemInBuf) 
    {
    	 ItemsTypeEnum type = ItemsTypeEnum.fromInt((int) sdesItemInBuf.readByte()); 
    	    
    	    int totalLength = sdesItemInBuf.readByte();
    	    
    	    Integer prefixLength = null;
    	    String prefix = null;

    	    if (type == ItemsTypeEnum.PRIV && sdesItemInBuf.isReadable(1)) 
    	    {
    	        prefixLength = (int) sdesItemInBuf.readByte(); 
    	        prefix = sdesItemInBuf.readBytes(prefixLength).toString(); 
    	        totalLength += (prefixLength + 1);
    	    }

    	    String data = sdesItemInBuf.readBytes(totalLength).toString(); 

    	    if (type == ItemsTypeEnum.PRIV) 
    	    {
    	        return new SdesItem(type, totalLength, prefixLength, prefix, data);
    	    } 
    	    else 
    	    {
    	        return new SdesItem(type, totalLength, data); 
    	    }
    	
    }
    
    public ReportBlock decodeReportBlock(ByteBuf buf) 
    {
        int ssrc = buf.readInt();
        
        byte fractionLost = buf.readByte();
        
        int cumulativePacketsLost = buf.readMedium();
        
        int extendedHighestSeqNumber = buf.readInt();
      
        int interarrivalJitter = buf.readInt();
     
        int lastSenderReport = buf.readInt();
        
        int delaySinceLastSenderReport = buf.readInt();

        return new ReportBlock(ssrc, fractionLost, cumulativePacketsLost,extendedHighestSeqNumber, interarrivalJitter, lastSenderReport, delaySinceLastSenderReport);
    }
    
}
