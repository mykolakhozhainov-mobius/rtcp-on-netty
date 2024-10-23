package edu.netty.common.message.rtcp.parser;

import edu.netty.common.message.rtcp.header.RtcpHeader;
import edu.netty.common.message.rtcp.packet.*;
import edu.netty.common.message.rtcp.parts.ReportBlock;
import edu.netty.common.message.rtcp.parts.chunk.Chunk;
import edu.netty.common.message.rtcp.parts.chunk.SdesItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class RtcpEncoder 
{
	 public ByteBuf encodeHeader(RtcpHeader header) 
	 {
	        ByteBuf headerInBuf = Unpooled.buffer(4); 
	        
	        headerInBuf.writeByte((header.getVersion() << 6) | (header.getIsPadding() ? 0x20 : 0) | (header.getItemCount() & 0x1F));  
	        headerInBuf.writeByte(header.getPacketType().getValue() & 0xFF);
	        headerInBuf.writeShort(header.getLength());

	        return headerInBuf;
	  }
	 
	 public ByteBuf encodeApp(ApplicationDefined app)
	 {
		 ByteBuf appInBuf = Unpooled.buffer(16);
		 
		 RtcpHeader header = app.getHeader();
		 
		 appInBuf.writeBytes(encodeHeader(header));
		 appInBuf.writeInt(app.getSSRC());
		 
		 byte[] nameBytes = app.getName().getBytes();
		 appInBuf.writeBytes(nameBytes);
		 
		 appInBuf.writeInt(app.getApplicationDependentData());

		 return appInBuf;
	 }
	 
	 public ByteBuf encodeBye(Bye bye)
	 {
		 int baseSize = 8;
		 int optReasonSize = 4;
		 int totalSize = baseSize;
		 
		 if (bye.getLengthOfReason() != null && bye.getReason() != null && !bye.getReason().isEmpty()) 
		    {
		        totalSize += optReasonSize; 
		    }
		 
		 ByteBuf byeInBuf = Unpooled.buffer(totalSize);
		 
		 RtcpHeader header = bye.getHeader();
		 byeInBuf.writeBytes(encodeHeader(header));
		 byeInBuf.writeInt(bye.getSSRC());
		 
		 if (bye.getLengthOfReason() != null && bye.getReason() != null && !bye.getReason().isEmpty()) 
		    {
		        byeInBuf.writeByte(bye.getLengthOfReason());
		        byeInBuf.writeBytes(bye.getReason().getBytes());
		    }
		 
		 return byeInBuf;
	 }
	 
	 public ByteBuf encodeReceiverReport(ReceiverReport rr)
	 {
		
		int baseSize = 8;
		    
		int reportBlockSize = 24;
		
		int ItemCount = rr.getReportBlocks().size();
		    
		int totalSize = baseSize + (ItemCount * reportBlockSize);
		    
		ByteBuf rrInBuf = Unpooled.buffer(totalSize);
		    
		RtcpHeader header = rr.getHeader(); 
		rrInBuf.writeBytes(encodeHeader(header));
		rrInBuf.writeInt(rr.getSSRC());
		 
		if(ItemCount>0) 
		{
			
		for (int i = 0; i < ItemCount; i++) 
		{
		    rrInBuf.writeBytes(encodeReportBlock(rr.getReportBlocks().get(i))); 
		}
		
		}
		    return rrInBuf;
	 }		
	 
	 public ByteBuf encodeSenderReport(SenderReport sr)
	 {
		
		int baseSize = 8 + 20 ;
		    
		int reportBlockSize = 24;
		
		int ItemCount = sr.getReportBlocks().size();
		
		int totalSize = baseSize + (ItemCount * reportBlockSize);
		
		ByteBuf profileSpecificExtensions = sr.getProfileSpecificExtensions();
		
		if (profileSpecificExtensions != null) 
		{
		   totalSize += profileSpecificExtensions.readableBytes(); 
		} 
		
		ByteBuf srInBuf = Unpooled.buffer(totalSize);
		    
		RtcpHeader header = sr.getHeader(); 
		srInBuf.writeBytes(encodeHeader(header));
		
		srInBuf.writeInt(sr.getSSRC());
		srInBuf.writeInt(sr.getNtpTimestampMostSignificant());
		srInBuf.writeInt(sr.getNtpTimestampLeastSignificant());
		srInBuf.writeInt(sr.getRtpTimestamp());
		srInBuf.writeInt(sr.getSenderPacketCount());
		srInBuf.writeInt(sr.getSenderOctetCount());
		 
		if(ItemCount>0)
		{
		for (int i = 0; i < ItemCount; i++) 
		{
		    srInBuf.writeBytes(encodeReportBlock(sr.getReportBlocks().get(i))); 
		}
		
		}
		
		if (profileSpecificExtensions != null) 
		{
		     srInBuf.writeBytes(profileSpecificExtensions); 
		}
		    
		    return srInBuf;
	 	}	
	 
	 public ByteBuf encodeSourceDescription(SourceDescription sd)
	 {
		 int baseSize = 4;
		 int ChunkSize = 8;
		 
		 int ItemCount = sd.getChunks().size();
		 
		 int totalSize = baseSize + (ItemCount * ChunkSize);
				 
		 ByteBuf sdInBuf = Unpooled.buffer(totalSize);
		    
		 RtcpHeader header = sd.getHeader(); 
		 
		 sdInBuf.writeBytes(encodeHeader(header));
		 
		 if(ItemCount>0)
		 {
			 for (int i = 0; i < ItemCount; i++) 
			 {
				 sdInBuf.writeBytes(encodeChunk(sd.getChunks().get(i))); 
			 }
			
		 }
		 
		 return sdInBuf;
	 }
	 
	 public ByteBuf encodeChunk(Chunk chunk) {
		   
		    int totalSdesSize = 4;
		    
		    for (SdesItem item : chunk.getItems()) 
		    {
		        totalSdesSize += encodeSdesItem(item).readableBytes(); 
		    }
		  
		    ByteBuf chunkInBuf = Unpooled.buffer(totalSdesSize);
		    
		    chunkInBuf.writeInt(chunk.getSsrc());

		    for (SdesItem item : chunk.getItems()) 
		    {
		        ByteBuf sdesItemInBuf = encodeSdesItem(item);
		        chunkInBuf.writeBytes(sdesItemInBuf); 
		    }

		    return chunkInBuf;
		}
	 
	 public ByteBuf encodeReportBlock(ReportBlock reportBlock) 
	 {
	     ByteBuf reportBlockInBuf = Unpooled.buffer(24); 

	     reportBlockInBuf.writeInt(reportBlock.getSsrc());
	     reportBlockInBuf.writeByte(reportBlock.getFractionLost());
	     
	     reportBlockInBuf.writeMedium(reportBlock.getCumulativePacketsLost());
	     
	     reportBlockInBuf.writeInt(reportBlock.getExtendedHighestSeqNumber());
	     reportBlockInBuf.writeInt(reportBlock.getInterarrivalJitter());
	     reportBlockInBuf.writeInt(reportBlock.getLastSenderReport());
	     reportBlockInBuf.writeInt(reportBlock.getDelaySinceLastSenderReport());

	     return reportBlockInBuf;
	 }
	 
	 public ByteBuf encodeSdesItem(SdesItem item) 
	 {
		    int totalLength = item.getLength(); 
		    
		    if (item.getPrefix() != null && !item.getPrefix().isEmpty()) 
		    {
		        totalLength += item.getPrefix().length() + item.getPrefixLength(); 
		       
		    }

		    ByteBuf SdesitemInBuf = Unpooled.buffer(1 + 1 + totalLength); 

		    SdesitemInBuf.writeByte(item.getType().getValue());

		    SdesitemInBuf.writeByte(totalLength);
		 
		    if (item.getPrefix() != null && !item.getPrefix().isEmpty()) 
		    {
		    	SdesitemInBuf.writeInt(item.getPrefixLength());
		        SdesitemInBuf.writeBytes(item.getPrefix().getBytes()); 
		    }

		    SdesitemInBuf.writeBytes(item.getData().getBytes());
		    
		    return SdesitemInBuf;
	  }
	 
 	}
