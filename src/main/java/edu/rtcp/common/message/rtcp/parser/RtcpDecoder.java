package edu.rtcp.common.message.rtcp.parser;

import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.packet.*;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.common.message.rtcp.parts.chunk.Chunk;
import edu.rtcp.common.message.rtcp.parts.chunk.SdesItem;
import edu.rtcp.common.message.rtcp.types.ItemsTypeEnum;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
 
public class RtcpDecoder 
{
    public RtcpHeader decodeHeader(ByteBuf headerInBuf) {
        byte firstByte = headerInBuf.readByte();

        byte version = (byte) ((firstByte >> 6) & 0x03);
        boolean isPadding = (firstByte & 0x20) != 0;
        byte itemCount = (byte) (firstByte & 0x1F);
 
        int typeInInt = ((int) headerInBuf.readByte()) & 0xFF;
        PacketTypeEnum packetTypeValue = PacketTypeEnum.fromInt(typeInInt);
 
        short length = headerInBuf.readShort();
        
        return new RtcpHeader(version, isPadding, itemCount, packetTypeValue, length);
    }
    
    public ApplicationDefined decodeApp(ByteBuf appInBuf) {
        RtcpHeader header = decodeHeader(appInBuf);
        int ssrc = appInBuf.readInt();
      
        byte[] nameBytes = new byte[4];
        appInBuf.readBytes(nameBytes); 
             
        String name = new String(nameBytes); 
        
        int applicationDependentData = appInBuf.readInt();
        
        return new ApplicationDefined(header, ssrc, name, applicationDependentData);
    }
    
    public Bye decodeBye(ByteBuf byeInBuf) {
        RtcpHeader header = this.decodeHeader(byeInBuf);
        int ssrc = byeInBuf.readInt();

        int reasonLength = 0;
        String reasonString = null;

        if (byeInBuf.isReadable()) {
            reasonLength = ((int) byeInBuf.readByte() & 0xFF);

            if (reasonLength > 0) {
                byte[] reasonBytes = new byte[reasonLength]; 
                byeInBuf.readBytes(reasonBytes);

                reasonString = new String(reasonBytes);
            }
        }
        
        Bye bye = new Bye(header, ssrc);
        
        if (reasonLength > 0)
        {
            bye.setLengthOfReason(reasonLength);
            bye.setReason(reasonString);
        }

        return bye;
    }
    
    public ReceiverReport decodeReceiverReport(ByteBuf rrInBuf) {
        RtcpHeader header = decodeHeader(rrInBuf);
        int ssrc = rrInBuf.readInt();
        
        int itemCount = header.getItemCount(); 
        
        ReceiverReport rr = new ReceiverReport(header, ssrc);
        
        if (itemCount > 0) {
            List<ReportBlock> reportBlocks = new ArrayList<>(itemCount);

            for (int i = 0; i < itemCount; i++) {
                reportBlocks.add(decodeReportBlock(rrInBuf));
                rr.setReportBlocks(reportBlocks);
            }
        }

        return rr;
    }
    
    public SenderReport decodeSenderReport(ByteBuf srInBuf) {
        RtcpHeader header = decodeHeader(srInBuf.readBytes(4));
        
        int ssrc = srInBuf.readInt();
        int ntpTimestampMostSignificant = srInBuf.readInt();
        int ntpTimestampLeastSignificant = srInBuf.readInt();
        int rtpTimestamp = srInBuf.readInt();
        int senderPacketCount = srInBuf.readInt();
        int senderOctetCount = srInBuf.readInt();

        int itemCount = header.getItemCount();
        
        SenderReport sr = new SenderReport(
                header,
                ssrc,
                ntpTimestampMostSignificant,
                ntpTimestampLeastSignificant,
                rtpTimestamp,
                senderPacketCount,
                senderOctetCount
        );

        if (itemCount > 0) {
            List<ReportBlock> reportBlocks = new ArrayList<>(itemCount);
        
            for (int i = 0; i < itemCount; i++) {
                reportBlocks.add(decodeReportBlock(srInBuf.readBytes(24)));
                sr.setReportBlocks(reportBlocks);
            }
        }
        
        return sr;
    }
    
    public SourceDescription decodeSourceDescription(ByteBuf sdInBuf) {
        RtcpHeader header = decodeHeader(sdInBuf.readBytes(4));

        int ssrc = sdInBuf.readInt();
        SourceDescription sd = new SourceDescription(header, ssrc);

        int itemCount = header.getItemCount(); 
        List<Chunk> chunks = new ArrayList<>(itemCount);

        for (int i = 0; i < itemCount; i++) {
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
