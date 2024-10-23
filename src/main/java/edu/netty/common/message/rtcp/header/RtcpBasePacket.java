package edu.netty.common.message.rtcp.header;

public abstract class RtcpBasePacket 
{
    protected RtcpHeader header;

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

}
