package edu.rtcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.packet.ApplicationDefined;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.common.message.rtcp.packet.SourceDescription;
import edu.rtcp.common.message.rtcp.parser.RtcpDecoder;
import edu.rtcp.common.message.rtcp.parser.RtcpEncoder;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.common.message.rtcp.parts.chunk.Chunk;
import edu.rtcp.common.message.rtcp.parts.chunk.SdesItem;
import edu.rtcp.common.message.rtcp.types.ItemsTypeEnum;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class RtcpDecoderTest {

    private RtcpDecoder decoder = new RtcpDecoder();
    private RtcpEncoder encoder = new RtcpEncoder();

    @Test
    public void testDecodeHeader() 
    {
        ByteBuf buf = Unpooled.buffer();
        
        buf.writeByte(0x81);  // version = 2, padding = 0, itemCount = 1
        buf.writeByte(200);   // packet type
        buf.writeShort(4);    // length
        
        int bytesRead = buf.readableBytes();
        
        RtcpHeader header = decoder.decodeHeader(buf);

        System.out.println("Header = " +bytesRead);
        
        assertNotNull(header);
        assertEquals(2, header.getVersion());
        assertEquals(false, header.getIsPadding());
        assertEquals(1, header.getItemCount());
        assertEquals(PacketTypeEnum.fromInt(200), header.getPacketType());
        assertEquals(4, header.getLength());
        
    }

    @Test
    public void testDecodeApp() 
    {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0x80);  // version = 2, padding = 0, itemCount = 0
        buf.writeByte(PacketTypeEnum.APP.getValue());
        buf.writeShort(4);    // length
        buf.writeInt(123456789); // SSRC
        buf.writeBytes("TEST".getBytes()); // name
        buf.writeInt(987654321); // application-dependent data
        
        int bytesRead = buf.readableBytes();
        
        ApplicationDefined app = decoder.decodeApp(buf);

        System.out.println("App = " +bytesRead);
        
        assertNotNull(app);
        assertEquals(123456789, app.getSSRC());
        assertEquals("TEST", app.getName());
        assertEquals(987654321, app.getApplicationDependentData());
    }

    @Test
    public void testDecodeBye() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0x81);  // version = 2, padding = 0, itemCount = 1
        buf.writeByte(PacketTypeEnum.BYE.getValue());
        buf.writeShort(4);    // length
        buf.writeInt(123456789); // SSRC
        buf.writeByte(3);     // reason length
        buf.writeBytes("TES".getBytes()); // reason
        
        int bytesRead = buf.readableBytes();
        
        Bye bye = decoder.decodeBye(buf);
        
        System.out.println("Bye = " +bytesRead);

        assertNotNull(bye);
        assertEquals(123456789, bye.getSSRC());
        assertEquals(3, bye.getLengthOfReason());
        assertEquals("TES", bye.getReason());
    }

    @Test
    public void testDecodeReceiverReport() 
    {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0x81);  // version = 2, padding = 0, itemCount = 1
        buf.writeByte(PacketTypeEnum.RECEIVER_REPORT.getValue());
        buf.writeShort(4);    // length
        
        buf.writeInt(123456789); // SSRC
        
        buf.writeInt(123456789); // Report Block SSRC
        buf.writeByte(11);    // Fraction lost
        buf.writeMedium(12);  // Cumulative packets lost
        buf.writeInt(13);     // Extended Highest Seq Num
        buf.writeInt(14);     // Interarrival Jitter
        buf.writeInt(15);     // Last SR Timestamp
        buf.writeInt(16);     // Delay Since Last SR

        int bytesRead = buf.readableBytes();
        
        ReceiverReport rr = decoder.decodeReceiverReport(buf);
        
        System.out.println("Bye = " +bytesRead);
        
        assertNotNull(rr);
        assertEquals(123456789, rr.getSSRC());

        List<ReportBlock> reportBlocks = rr.getReportBlocks();
        assertEquals(1, reportBlocks.size());

        ReportBlock rb = reportBlocks.get(0);
        
        assertEquals(123456789, rb.getSsrc());
        
        assertEquals(11, rb.getFractionLost());
        assertEquals(12, rb.getCumulativePacketsLost());
        
        assertEquals(13, rb.getExtendedHighestSeqNumber());
        assertEquals(14, rb.getInterarrivalJitter());
        assertEquals(15, rb.getLastSenderReport());
        assertEquals(16, rb.getDelaySinceLastSenderReport());
    }

    @Test
    public void testDecodeSenderReport() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0x81);  // version = 2, padding = 0, itemCount = 1
        buf.writeByte(PacketTypeEnum.SENDER_REPORT.getValue());
        buf.writeShort(0);    // length
        
        buf.writeInt(123456789); // SSRC
        
        buf.writeInt(10);     // NTP Timestamp most significant
        buf.writeInt(1);      // NTP Timestamp least significant
        buf.writeInt(1);      // RTP Timestamp
        buf.writeInt(15);     // Sender's packet count
        buf.writeInt(0);      // Sender's octet count
        
        buf.writeInt(123456789); // Report Block SSRC
        buf.writeByte(20);    // Fraction lost
        buf.writeMedium(30);  // Cumulative packets lost
        buf.writeInt(40);     // Extended Highest Seq Num
        buf.writeInt(50);     // Interarrival Jitter
        buf.writeInt(60);     // Last SR Timestamp
        buf.writeInt(70);     // Delay Since Last SR
        
        int length = buf.readableBytes();
        buf.setShort(2, length); 
       
        buf.readerIndex(0);
 
        
        SenderReport sr = decoder.decodeSenderReport(buf);
        
        assertEquals(52, sr.getHeader().getLength());
        assertNotNull(sr);
        assertEquals(123456789, sr.getSSRC());
        assertEquals(10, sr.getNtpTimestampMostSignificant());
        assertEquals(1, sr.getNtpTimestampLeastSignificant());
        assertEquals(1, sr.getRtpTimestamp());
        assertEquals(15, sr.getSenderPacketCount());
        assertEquals(0, sr.getSenderOctetCount());

        List<ReportBlock> reportBlocks = sr.getReportBlocks();
        assertEquals(1, reportBlocks.size());

        ReportBlock rb = reportBlocks.get(0);
        assertEquals(123456789, rb.getSsrc());
        assertEquals(20, rb.getFractionLost());
        assertEquals(30, rb.getCumulativePacketsLost());
        assertEquals(40, rb.getExtendedHighestSeqNumber());
        assertEquals(50, rb.getInterarrivalJitter());
        assertEquals(60, rb.getLastSenderReport());
        assertEquals(70, rb.getDelaySinceLastSenderReport());
    }

    @Test
    public void testDecodeSourceDescription() 
    {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0x81);  // version = 2, padding = 0, itemCount = 1
        buf.writeByte(PacketTypeEnum.SOURCE_DESCRIPTION.getValue());
        buf.writeShort(1);    // length
        
        int ssrc = 123456789;
        buf.writeInt(ssrc);  

        SdesItem loc = new SdesItem(ItemsTypeEnum.CNAME, 4, "re");

        buf.writeByte(loc.getType().getValue());
        buf.writeByte(loc.getLength());   
        buf.writeBytes(loc.getData().getBytes(StandardCharsets.UTF_8));
        
        int bytesRead = buf.readableBytes();
        System.out.println("SD = " + bytesRead); 

        SourceDescription sd = decoder.decodeSourceDescription(buf);

        assertNotNull(sd);
        List<Chunk> chunks = sd.getChunks();
        assertEquals(1, chunks.size());

        Chunk chunk = chunks.get(0);
        assertEquals(ssrc, chunk.getSsrc());

        List<SdesItem> items = chunk.getItems();
        assertEquals(1, items.size()); 
          
        SdesItem item = items.get(0);
        assertEquals(ItemsTypeEnum.CNAME, item.getType());
        assertEquals("re", item.getData());
    }
    
    @Test
    public void testDecodeSdesItem() 
    {
        ItemsTypeEnum type = ItemsTypeEnum.CNAME;
        String data = "re";
        int length = 2 + data.length();
        
        SdesItem cname = new SdesItem(type, length, data);
        
        ByteBuf result = encoder.encodeSdesItem(cname);

        int bytesRead = result.readableBytes();
        System.out.println("SdesItem = " + bytesRead);

        SdesItem item = decoder.decodeSdesItem(result);
        
        assertNotNull(item);
        assertEquals(type, item.getType());
        assertEquals(4, item.getLength());  
    }
}