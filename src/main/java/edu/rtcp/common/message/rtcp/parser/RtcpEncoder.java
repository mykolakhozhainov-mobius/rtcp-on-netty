package edu.rtcp.common.message.rtcp.parser;

import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.packet.*;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.common.message.rtcp.parts.chunk.Chunk;
import edu.rtcp.common.message.rtcp.parts.chunk.SdesItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

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
		 appInBuf.writeBytes(Arrays.copyOfRange(nameBytes, 0, 4));
		 
		 appInBuf.writeInt(app.getApplicationDependentData());
		 
		 //правильно встановлює довжину пакета
		 header.setLength((short) (appInBuf.readableBytes())); 
	     appInBuf.setShort(2, header.getLength());

		 return appInBuf;
	 }
	 

	 public ByteBuf encodeBye(Bye bye) 
	 {
		 final int baseSize = 8;
		 final int optReasonSize = 4;

		 int totalSize = baseSize;
		 
		 if (bye.getReason() != null && !bye.getReason().isEmpty()) 
		 {
			 totalSize += optReasonSize;
		 }
		 
		 ByteBuf byeInBuf = Unpooled.buffer(totalSize);
		 
		 RtcpHeader header = bye.getHeader();
		 byeInBuf.writeBytes(encodeHeader(header));
		 byeInBuf.writeInt(bye.getSSRC());
		 
		 if (bye.getReason() != null && !bye.getReason().isEmpty()) 
		 {
		     byeInBuf.writeByte(bye.getReason().length() & 0xFF); 
		     byeInBuf.writeBytes(bye.getReason().getBytes(StandardCharsets.UTF_8)); 
		 }
		 
		//правильно встановлює довжину пакета
		 header.setLength((short) (byeInBuf.readableBytes())); 
	     byeInBuf.setShort(2, header.getLength());

		 
		 return byeInBuf;
	 }
	 

	 public ByteBuf encodeReceiverReport(ReceiverReport rr) 
	 {

		final int baseSize = 8;
		final int reportBlockSize = 24;

		List<ReportBlock> reportBlocks = rr.getReportBlocks();

		int itemCount = reportBlocks != null ? rr.getReportBlocks().size() : 0;
		int totalSize = baseSize + (itemCount * reportBlockSize);
		    
		ByteBuf rrInBuf = Unpooled.buffer(totalSize);
		    
		RtcpHeader header = rr.getHeader(); 
		rrInBuf.writeBytes(encodeHeader(header));
		rrInBuf.writeInt(rr.getSSRC());
		 
		if (itemCount>0) {
			for (int i = 0; i < itemCount; i++) {
		    	rrInBuf.writeBytes(encodeReportBlock(rr.getReportBlocks().get(i)));
			}
		}
		
		//правильно встановлює довжину пакета
		 header.setLength((short) (rrInBuf.readableBytes())); 
		 rrInBuf.setShort(2, header.getLength());
	     
		return rrInBuf;
	 }		
	 
	 public ByteBuf encodeSenderReport(SenderReport sr) 
	 {
     
		final int baseSize = 8 + 20 ;
		final int reportBlockSize = 24;
		
		int itemCount = sr.getReportBlocks() == null ? 0 : sr.getReportBlocks().size();
		
		int totalSize = baseSize + (itemCount * reportBlockSize);
		
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
		 
		if (itemCount > 0) 
		{
			for (int i = 0; i < itemCount; i++) 
			{
		    	srInBuf.writeBytes(encodeReportBlock(sr.getReportBlocks().get(i)));
			}
		}
		
		if (profileSpecificExtensions != null) 
		{
		     srInBuf.writeBytes(profileSpecificExtensions); 
		}
		
		//правильно встановлює довжину пакета
		 header.setLength((short) (srInBuf.readableBytes())); 
		 srInBuf.setShort(2, header.getLength());
		    
		return srInBuf;
	 }
	 

	 public ByteBuf encodeSourceDescription(SourceDescription sd)
	 {
		 final int baseSize = 4;
		 final int chunkSize = 8;
		 
		 int itemCount = sd.getChunks() != null ? sd.getChunks().size() : 0;
		 
		 int totalSize = baseSize + (itemCount * chunkSize);
				 
		 ByteBuf sdInBuf = Unpooled.buffer(totalSize);
		    
		 RtcpHeader header = sd.getHeader(); 
		 
		 sdInBuf.writeBytes(encodeHeader(header));
		 
		 if (itemCount > 0) {
			 for (int i = 0; i < itemCount; i++) {
				 sdInBuf.writeBytes(encodeChunk(sd.getChunks().get(i))); 
			 }
		 }
		 
		//правильно встановлює довжину пакета
		 header.setLength((short) (sdInBuf.readableBytes())); 
		 sdInBuf.setShort(2, header.getLength());    
		 
		 return sdInBuf;
	 }
	 

	 public ByteBuf encodeChunk(Chunk chunk)
	 {

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
		    int totalLength = 2 + item.getData().length(); 


		    if (item.getPrefix() != null && !item.getPrefix().isEmpty()) 
		    {
		        totalLength += 1 + item.getPrefix().length();
		    }

		    ByteBuf SdesItemInBuf = Unpooled.buffer(totalLength);
		    
		    SdesItemInBuf.writeByte(item.getType().getValue());
		    SdesItemInBuf.writeByte(item.getLength());

		    if (item.getPrefix() != null && !item.getPrefix().isEmpty()) 
		    {
		        SdesItemInBuf.writeByte(item.getPrefixLength());
		        SdesItemInBuf.writeBytes(item.getPrefix().getBytes(StandardCharsets.UTF_8));
		    }
		    
		    SdesItemInBuf.writeBytes(item.getData().getBytes(StandardCharsets.UTF_8));
		  
		    //правильно встановлює довжину 
		    int length = SdesItemInBuf.readableBytes();
		    item.setLength(length);
		    SdesItemInBuf.setByte(1, length);
		    
		    return SdesItemInBuf;
		}
}
