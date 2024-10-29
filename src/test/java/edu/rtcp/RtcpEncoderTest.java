package edu.rtcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.packet.ApplicationDefined;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.common.message.rtcp.packet.SourceDescription;
import edu.rtcp.common.message.rtcp.parser.RtcpEncoder;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.common.message.rtcp.parts.chunk.Chunk;
import edu.rtcp.common.message.rtcp.parts.chunk.SdesItem;
import edu.rtcp.common.message.rtcp.types.ItemsTypeEnum;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;
import io.netty.buffer.ByteBuf;

public class RtcpEncoderTest 
{
    private RtcpEncoder encoder;
    
    @Test
    public void testEncodeHeader() 
    {
    	encoder = new RtcpEncoder();
    	
        byte version = 2;
        boolean isPadding = false;
        byte itemCount = 1;
        PacketTypeEnum packetTypeValue = PacketTypeEnum.fromInt(200);
        short length = 0;

        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetTypeValue, length);

        ByteBuf result = encoder.encodeHeader(header);

        assertNotNull(result);
        assertEquals(4, result.readableBytes());
        
        int expectedFirstByte = 0x80 | (itemCount & 0x1F);
        assertEquals(expectedFirstByte, result.getByte(0) & 0xFF);
        assertEquals(200, result.getByte(1) & 0xFF);

    }
    
    @Test
    public void testEncodeApp() 
    {
    	encoder = new RtcpEncoder();
    	
        byte version = 2;
        boolean isPadding = false;
        byte itemCount = 1;
        PacketTypeEnum packetTypeValue = PacketTypeEnum.APP;
        
        short length = 0;

        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetTypeValue, length);

        int ssrc = 123456789;
        String name = "TEST"; 
        int applicationDependentData = 987654321;

        ApplicationDefined app = new ApplicationDefined(header, ssrc, name, applicationDependentData);

        ByteBuf result = encoder.encodeApp(app);
        
        System.out.println();
        
        assertNotNull(result);
  
        assertEquals(16, result.readableBytes());

        int expectedFirstByte = 0x80 | (itemCount & 0x1F);
        assertEquals(expectedFirstByte, result.getByte(0) & 0xFF);
        assertEquals(PacketTypeEnum.APP.getValue(), result.getByte(1) & 0xFF);

        assertEquals(ssrc, result.getInt(4)); 

        byte[] expectedNameBytes = name.getBytes();
        byte[] actualNameBytes = new byte[4];
        result.getBytes(8, actualNameBytes);
        assertEquals(new String(expectedNameBytes), new String(actualNameBytes));

        assertEquals(applicationDependentData, result.getInt(12));

        short expectedLength = (short) (result.readableBytes()); 
        System.out.println("Length in App: " + expectedLength);
        assertEquals(expectedLength, result.getShort(2));
        
    }
    
    
    @Test
    public void testEncodeBye() 
    {
        encoder = new RtcpEncoder();

        byte version = 2;
        boolean isPadding = false;
        byte itemCount = 1;
        PacketTypeEnum packetTypeValue = PacketTypeEnum.BYE;
        
        short length = 0; 
        
        int ssrc = 123456789;
        
        String reason = "TES";
        int lengthOfReason = reason.length(); 

        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetTypeValue, length);
        Bye bye = new Bye(header, ssrc);
        
        bye.setLengthOfReason(lengthOfReason); 
        bye.setReason(reason);

        ByteBuf result = encoder.encodeBye(bye);

        assertNotNull(result);
        
        int expectedFirstByte = 0x80 | (itemCount & 0x1F);
        assertEquals(expectedFirstByte, result.getByte(0) & 0xFF);
        assertEquals(ssrc, result.getInt(4));
        
       assertEquals(lengthOfReason, result.getByte(8));
       assertEquals(reason, result.toString(9, lengthOfReason, java.nio.charset.StandardCharsets.UTF_8));
        
        short expectedLength = (short) (result.readableBytes()); 
        assertEquals(expectedLength, result.getShort(2));
    }

    @Test
    public void testEncodeReceiverReport() 
    {
    	encoder = new RtcpEncoder();
    	    
    	byte version = 2;
    	boolean isPadding = false;
    	byte itemCount = 1; 
    	PacketTypeEnum packetTypeValue = PacketTypeEnum.RECEIVER_REPORT;
    	    
    	short length = 0; 
    	int ssrc = 123456789;

    	RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetTypeValue, length);
    	  
    	ReceiverReport rr = new ReceiverReport(header, ssrc);
    	  
    	ReportBlock reportBlock = new ReportBlock(ssrc, (byte) 11, 12, 13, 14, 15, 16); 
    	rr.setReportBlocks(List.of(reportBlock)); 

    	ByteBuf result = encoder.encodeReceiverReport(rr);

    	assertNotNull(result);
    	assertEquals(32, result.readableBytes()); 
    	assertEquals(ssrc, result.getInt(4)); 
    	
    	assertEquals(ssrc, result.getInt(8));  // Report Block: SSRC
        assertEquals(11, result.getByte(12));  // Report Block: Fraction Lost
        assertEquals(12, result.getUnsignedMedium(13));   // Report Block: Cumulative Lost
        assertEquals(13, result.getInt(16));   // Report Block: Extended Highest Seq Num
        assertEquals(14, result.getInt(20));   // Report Block: Interarrival Jitter
        assertEquals(15, result.getInt(24));   // Report Block: Last SR Timestamp
        assertEquals(16, result.getInt(28));   // Report Block: Delay Since Last Sender Report
        
        assertTrue(result.readableBytes() >= 32);
		
		short expectedLength = (short) (result.readableBytes());
        System.out.println("Length in RR: " + expectedLength);
        assertEquals(expectedLength, result.getShort(2));
		 
    	}


    @Test
    public void testEncodeSenderReport() 
    {
    	encoder = new RtcpEncoder();
    	
        byte version = 2;
        boolean isPadding = false;
        byte itemCount = 1;
        PacketTypeEnum packetTypeValue = PacketTypeEnum.SENDER_REPORT;
        
        short length = 0;
        
        int ssrc = 123456789;
        int ntpTimestampMostSignificant = 10;
        int ntpTimestampLeastSignificant = 1;
        int rtpTimestamp = 1;
        int senderPacketCount = 15;
        int senderOctetCount = 0;

        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetTypeValue, length);
        
        SenderReport sr = new SenderReport(header, ssrc, ntpTimestampMostSignificant, ntpTimestampLeastSignificant, rtpTimestamp, senderPacketCount, senderOctetCount);
        
        ReportBlock reportBlock = new ReportBlock(ssrc, (byte) 20, 30, 40, 50, 60, 70); 
    	sr.setReportBlocks(List.of(reportBlock)); 

        ByteBuf result = encoder.encodeSenderReport(sr);

        assertEquals(52, result.readableBytes());

        // Check SSRC
        assertEquals(ssrc, result.getInt(4)); 

        // Check NTP Timestamp most significant word
        assertEquals(ntpTimestampMostSignificant, result.getInt(8)); 

        // Check NTP Timestamp least significant word
        assertEquals(ntpTimestampLeastSignificant, result.getInt(12)); 

        // Check RTP Timestamp
        assertEquals(rtpTimestamp, result.getInt(16));

        // Check sender's packet count
        assertEquals(senderPacketCount, result.getInt(20)); 

        // Check sender's octet count
        assertEquals(senderOctetCount, result.getInt(24)); 

        // Check Report Block
        assertEquals(ssrc, result.getInt(28));  // Report Block: SSRC
        assertEquals(20, result.getByte(32));  // Report Block: Fraction Lost
        assertEquals(30, result.getUnsignedMedium(33));   // Report Block: Cumulative Lost
        assertEquals(40, result.getInt(36));   // Report Block: Extended Highest Seq Num
        assertEquals(50, result.getInt(40));   // Report Block: Interarrival Jitter
        assertEquals(60, result.getInt(44));   // Report Block: Last SR Timestamp
        assertEquals(70, result.getInt(48));   // Report Block: Delay Since Last Sender Report
       
        assertTrue(result.readableBytes() >= 52); 

        short expectedLength = (short) (result.readableBytes());
        System.out.println("Length in SR: " + expectedLength);
        assertEquals(expectedLength, result.getShort(2)); 
    }
    

    @Test
    public void testEncodeSourceDescription() 
    {
    	encoder = new RtcpEncoder();
    	
        byte version = 2;
        boolean isPadding = false;
        byte itemCount = 1;
        PacketTypeEnum packetTypeValue = PacketTypeEnum.SOURCE_DESCRIPTION;
        
        short length = 0;
        
        int ssrc = 123456789;

        RtcpHeader header = new RtcpHeader(version, isPadding, itemCount, packetTypeValue, length);
        
        SourceDescription sd = new SourceDescription(header);
        
        SdesItem loc = new SdesItem(ItemsTypeEnum.LOC, 11, "ee");
        
        List<SdesItem> items = new ArrayList<>();
        items.add(loc);
        Chunk chunks = new Chunk(ssrc,items);
        
        List<Chunk> ch = new ArrayList<>();
        ch.add(chunks);
        
        sd.setChunks(ch);

        ByteBuf result = encoder.encodeSourceDescription(sd);
        
        short expectedLength = (short) (result.readableBytes());
        System.out.println("Length in SD: " + expectedLength);
        
        assertNotNull(result);
        assertEquals(12, result.readableBytes()); 
       
    }

    @Test
    public void testEncodeChunk() 
    {
    	encoder = new RtcpEncoder();
    
        int ssrc = 123456789;
        
        SdesItem loc = new SdesItem(ItemsTypeEnum.LOC, 11, "re");
        List<SdesItem> items = new ArrayList<>();
        items.add(loc);
        
        Chunk chunk = new Chunk(ssrc,items);
  
        ByteBuf result = encoder.encodeChunk(chunk);
        
        short expectedLength = (short) (result.readableBytes());
        System.out.println("Length in Chunk: " + expectedLength);
        assertNotNull(result);
        assertEquals(8, result.readableBytes()); 
        assertEquals(ssrc, result.getInt(0));
    }

    @Test
    public void testEncodeReportBlock() 
    {
    	encoder = new RtcpEncoder();
    	
        ReportBlock rb = new ReportBlock(123456789, (byte) 0, 0, 0, 0, 0, 0);

        ByteBuf result = encoder.encodeReportBlock(rb);
        
        short expectedLength = (short) (result.readableBytes());
        
        assertNotNull(result);
        assertEquals(24, result.readableBytes()); 
        assertEquals(123456789, result.getInt(0));
        
        System.out.println("Length in ReportBlock: " + expectedLength);
    }

    @Test
    public void testEncodeSdesItem() 
    {
    	encoder = new RtcpEncoder();
    	
        SdesItem item = new SdesItem(ItemsTypeEnum.LOC, 2,"et");
    
        ByteBuf result = encoder.encodeSdesItem(item);
      
        short expectedLength = (short) (result.readableBytes());
        
        assertNotNull(result);
        assertEquals(4, expectedLength);
        
        System.out.println("Length in SdesItem: " + expectedLength);
    
    }

}
    