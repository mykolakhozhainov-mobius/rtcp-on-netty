package edu.netty.common.message.rtcp.header;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class RTCPBasePacket 
{
    protected RTCPHeader header;

    public RTCPBasePacket(RTCPHeader header) 
    {
        this.header = header;
    }

    public RTCPHeader getHeader() 
    {
        return this.header;
    }

    public void setHeader(RTCPHeader header) 
    {
        this.header = header;
    }

    protected ByteBuf headerToByteBuf() 
    {
        ByteBuf headerInBuffer = Unpooled.buffer(8); 
        
        headerInBuffer.setByte(0, (byte) (((header.getVersion() & 0x03) << 6) | 
                                   ((header.getIsPadding() ? 1 : 0) << 5) | 
                                   (header.getItemCount() & 0x1F))); 
        
        headerInBuffer.setByte(1, (byte) (header.getPacketType().getValue() & 0xFF));
        headerInBuffer.setByte(2, (byte) ((header.getLength() >> 8) & 0xFF)); 
        headerInBuffer.setByte(3, (byte) (header.getLength() & 0xFF)); 
        headerInBuffer.setByte(4, (byte) ((header.getSSRC() >> 24) & 0xFF)); 
        headerInBuffer.setByte(5, (byte) ((header.getSSRC() >> 16) & 0xFF)); 
        headerInBuffer.setByte(6, (byte) ((header.getSSRC() >> 8) & 0xFF)); 
        headerInBuffer.setByte(7, (byte) (header.getSSRC() & 0xFF)); 

        return headerInBuffer; 
    }
}
