package edu.netty.common.message.rtcp.packet;

import java.util.List;

import edu.netty.common.message.rtcp.header.RTCPBasePacket;
import edu.netty.common.message.rtcp.header.RTCPHeader;
import edu.netty.common.message.rtcp.parts.ReportBlock;
import io.netty.buffer.ByteBuf;


/*
	   		0                   1                   2                   3
	        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	header |V=2|P|    RC   |   PT=SR=200   |             length            |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                         SSRC of sender                        |
	       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	sender |              NTP timestamp, most significant word             |
	info   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |             NTP timestamp, least significant word             |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                         RTP timestamp                         |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                     sender's packet count                     |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                      sender's octet count                     |
	       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	report |                 SSRC_1 (SSRC of first source)                 |
	block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	  1    | fraction lost |       cumulative number of packets lost       |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |           extended highest sequence number received           |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                      interarrival jitter                      |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                         last SR (LSR)                         |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                   delay since last SR (DLSR)                  |
	       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	report |                 SSRC_2 (SSRC of second source)                |
	block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	  2    :                               ...                             :
	       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	       |                  profile-specific extensions                  |
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
*/

public class SenderReport extends RTCPBasePacket 
{

    private Integer ntpTimestampMostSignificant;
    private Integer ntpTimestampLeastSignificant; 
    private Integer rtpTimestamp;
    private Integer senderPacketCount;
    private Integer senderOctetCount;
    private List<ReportBlock> reportBlock;
    private ByteBuf profileSpecificExtensions;

    public SenderReport(RTCPHeader header,Integer ntpTimestampMostSignificant,Integer ntpTimestampLeastSignificant, Integer rtpTimestamp, Integer senderPacketCount, Integer senderOctetCount) 
    {
        super(header);
        setNtpTimestampMostSignificant(ntpTimestampMostSignificant);
        setNtpTimestampMostSignificant(ntpTimestampMostSignificant);
        setRtpTimestamp(rtpTimestamp);
        setSenderPacketCount(senderPacketCount);
        setSenderOctetCount(senderOctetCount);
       
    }

    public Integer getNtpTimestampMostSignificant()
    {
        return ntpTimestampMostSignificant;
    }

    public void setNtpTimestampMostSignificant(Integer value) 
    {
        this.ntpTimestampMostSignificant = value;
    }

    public Integer getNtpTimestampLeastSignificant() 
    {
        return ntpTimestampLeastSignificant;
    }

    public void setNtpTimestampLeastSignificant(Integer value) 
    {
        this.ntpTimestampLeastSignificant = value;
    }

    public Integer getRtpTimestamp() 
    {
        return rtpTimestamp;
    }

    public void setRtpTimestamp(Integer value) 
    {
        this.rtpTimestamp = value;
    }

    public Integer getSenderPacketCount() 
    {
        return senderPacketCount;
    }

    public void setSenderPacketCount(Integer value) 
    {
        this.senderPacketCount = value;
    }

    public Integer getSenderOctetCount() 
    {
        return senderOctetCount;
    }

    public void setSenderOctetCount(Integer value) 
    {
        this.senderOctetCount = value;
    }
    
    public List<ReportBlock> getReportBlock()
    {
    	if(reportBlock==null)
    		return null;
    	
    	return reportBlock;
    }
    
    public void setReportBlock(List<ReportBlock> value) 
    {
        this.reportBlock = value;
    }

    public ByteBuf getProfileSpecificExtensions()
    {
    	if(profileSpecificExtensions==null)
    		return null;
    	
    	return profileSpecificExtensions;
    }
    
    public void setProfileSpecificExtensions(ByteBuf value) 
    {
        this.profileSpecificExtensions = value;
    }
}