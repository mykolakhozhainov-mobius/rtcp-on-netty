package edu.rtcp.server.callback.header;

public abstract class RtcpBasePacket {
    protected RtcpHeader header;
    protected int ssrc;

    public RtcpBasePacket(RtcpHeader header) 
    {
        this.header = header;
    }

    public RtcpHeader getHeader() 
    {
        return this.header;
    }

    public void setHeader(RtcpHeader header) 
    {
        this.header = header;
    }

    public int getSSRC() {
        return this.ssrc;
    };

    @Override
    public String toString() {
        return "\u001B[34m" + "=== RTCP Packet ===\n" +
               "Type: " + this.header.getPacketType() + "\n" +
               "Item count: " + this.header.getItemCount() + "\n" +
               "Session (SSRC): " + this.getSSRC() + "\u001B[0m";
    }
}
