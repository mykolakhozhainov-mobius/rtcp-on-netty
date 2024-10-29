package edu.rtcp.common.message.rtcp.parser;

import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.packet.*;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.common.message.rtcp.parts.chunk.Chunk;
import edu.rtcp.common.message.rtcp.parts.chunk.SdesItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;
import java.util.List;

public class RtcpEncoder {
	 public static ByteBuf encodeHeader(RtcpHeader header) {
	        ByteBuf headerInBuf = Unpooled.buffer(4); 
	        
	        headerInBuf.writeByte((header.getVersion() << 6) | (header.getIsPadding() ? 0x20 : 0) | (header.getItemCount() & 0x1F));  
	        headerInBuf.writeByte(header.getPacketType().getValue() & 0xFF);
	        headerInBuf.writeShort(header.getLength());

	        return headerInBuf;
	  }
	 
	 public static ByteBuf encodeApp(ApplicationDefined app) {
		 ByteBuf appInBuf = Unpooled.buffer(16);
		 
		 RtcpHeader header = app.getHeader();
		 
		 appInBuf.writeBytes(encodeHeader(header));
		 appInBuf.writeInt(app.getSSRC());
		 
		 byte[] nameBytes = app.getName().getBytes();
		 appInBuf.writeBytes(Arrays.copyOfRange(nameBytes, 0, 4));
		 
		 appInBuf.writeInt(app.getApplicationDependentData());

		 return appInBuf;
	 }
	 
	 public static ByteBuf encodeBye(Bye bye) {
		 final int baseSize = 8;
		 final int optReasonSize = 4;

		 int totalSize = baseSize;
		 
		 if (bye.getReason() != null && !bye.getReason().isEmpty()) {
			 totalSize += optReasonSize;
		 }
		 
		 ByteBuf byeInBuf = Unpooled.buffer(totalSize);
		 
		 RtcpHeader header = bye.getHeader();
		 byeInBuf.writeBytes(encodeHeader(header));
		 byeInBuf.writeInt(bye.getSSRC());
		 
		 if (bye.getReason() != null && !bye.getReason().isEmpty()) {
			 byeInBuf.writeByte(bye.getLengthOfReason() & 0xFF);
		     byeInBuf.writeBytes(bye.getReason().getBytes());
		 }
		 
		 return byeInBuf;
	 }
	 
	 public static ByteBuf encodeReceiverReport(ReceiverReport rr) {
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

		return rrInBuf;
	 }		
	 
	 public static ByteBuf encodeSenderReport(SenderReport sr) {
		final int baseSize = 8 + 20 ;
		final int reportBlockSize = 24;
		
		int itemCount = sr.getReportBlocks() == null ? 0 : sr.getReportBlocks().size();
		
		int totalSize = baseSize + (itemCount * reportBlockSize);
		
		ByteBuf profileSpecificExtensions = sr.getProfileSpecificExtensions();
		
		if (profileSpecificExtensions != null) {
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
		 
		if (itemCount > 0) {
			for (int i = 0; i < itemCount; i++) {
		    	srInBuf.writeBytes(encodeReportBlock(sr.getReportBlocks().get(i)));
			}
		}
		
		if (profileSpecificExtensions != null) {
		     srInBuf.writeBytes(profileSpecificExtensions); 
		}
		    
		return srInBuf;
	 }
	 
	 public static ByteBuf encodeSourceDescription(SourceDescription sd) {
		 final int baseSize = 4;
		 final int chunkSize = 8;
		 
		 int itemCount = sd.getChunks() != null ? sd.getChunks().size() : 0;
		 
		 int totalSize = baseSize + (itemCount * chunkSize);
				 
		 ByteBuf sdInBuf = Unpooled.buffer(totalSize);
		    
		 RtcpHeader header = sd.getHeader(); 
		 
		 sdInBuf.writeBytes(encodeHeader(header));
		 sdInBuf.writeInt(sd.getSSRC());
		 
		 if (itemCount > 0) {
			 for (int i = 0; i < itemCount; i++) {
				 sdInBuf.writeBytes(encodeChunk(sd.getChunks().get(i))); 
			 }
		 }
		 
		 return sdInBuf;
	 }
	 
	 public static ByteBuf encodeChunk(Chunk chunk) {
		 int totalSdesSize = 4;
		    
		 for (SdesItem item : chunk.getItems()) {
			 totalSdesSize += encodeSdesItem(item).readableBytes();
		 }
		  
		 ByteBuf chunkInBuf = Unpooled.buffer(totalSdesSize);
		    
		 chunkInBuf.writeInt(chunk.getSsrc());

		 for (SdesItem item : chunk.getItems()) {
		    ByteBuf sdesItemInBuf = encodeSdesItem(item);
		    chunkInBuf.writeBytes(sdesItemInBuf);
		 }

		 return chunkInBuf;
	 }
	 
	 public static ByteBuf encodeReportBlock(ReportBlock reportBlock) {
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
	 
	 public static ByteBuf encodeSdesItem(SdesItem item) {
		 int totalLength = item.getLength();
		    
		 if (item.getPrefix() != null && !item.getPrefix().isEmpty()) {
			 totalLength += item.getPrefix().length() + item.getPrefixLength();
		 }

		 ByteBuf SdesItemInBuf = Unpooled.buffer(1 + 1 + totalLength);

		 SdesItemInBuf.writeByte(item.getType().getValue());

		 SdesItemInBuf.writeByte(totalLength);
		 
		 if (item.getPrefix() != null && !item.getPrefix().isEmpty()) {
			 SdesItemInBuf.writeInt(item.getPrefixLength());
			 SdesItemInBuf.writeBytes(item.getPrefix().getBytes());
		 }

		 SdesItemInBuf.writeBytes(item.getData().getBytes());
		    
		 return SdesItemInBuf;
	 }
}
