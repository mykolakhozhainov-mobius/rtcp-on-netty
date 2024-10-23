package edu.rtcp.common.message.rtcp.packet;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.header.RtcpHeader;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import io.netty.buffer.ByteBuf;

import java.util.List;


/*
  
	6.4.2 RR: Receiver Report RTCP Packet
	
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

public class SenderReport extends RtcpBasePacket {
    private final int ntpTimestampMostSignificant;
    private final int ntpTimestampLeastSignificant;
    private final int rtpTimestamp;

    private final int senderPacketCount;
    private final int senderOctetCount;

    private List<ReportBlock> reportBlock;
    private ByteBuf profileSpecificExtensions;

    public SenderReport(
            RtcpHeader header,
            int ssrc,
            int ntpTimestampMostSignificant,
            int ntpTimestampLeastSignificant,
            int rtpTimestamp,
            int senderPacketCount,
            int senderOctetCount
    ) {
        super(header);
        this.ssrc = ssrc;

        this.ntpTimestampLeastSignificant = ntpTimestampLeastSignificant;
        this.ntpTimestampMostSignificant = ntpTimestampMostSignificant;
        this.rtpTimestamp = rtpTimestamp;
        this.senderPacketCount = senderPacketCount;
        this.senderOctetCount = senderOctetCount;
    }

    public int getNtpTimestampMostSignificant()
    {
        return ntpTimestampMostSignificant;
    }

    public int getNtpTimestampLeastSignificant() {
        return ntpTimestampLeastSignificant;
    }

    public int getRtpTimestamp()
    {
        return rtpTimestamp;
    }

    public int getSenderPacketCount()
    {
        return senderPacketCount;
    }

    public int getSenderOctetCount()
    {
        return senderOctetCount;
    }

    public List<ReportBlock> getReportBlocks() {
    	return reportBlock;
    }

    public void setReportBlocks(List<ReportBlock> value)
    {
        this.reportBlock = value;
    }

    public ByteBuf getProfileSpecificExtensions() {
    	return profileSpecificExtensions;
    }

    public void setProfileSpecificExtensions(ByteBuf value)
    {
        this.profileSpecificExtensions = value;
    }
}