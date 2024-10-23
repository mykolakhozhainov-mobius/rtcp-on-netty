package edu.netty.common.message.rtcp.packet;

import java.util.List;

import edu.netty.common.message.rtcp.header.RtcpBasePacket;
import edu.netty.common.message.rtcp.header.RtcpHeader;
import edu.netty.common.message.rtcp.parts.ReportBlock;


/*
 
	6.4.2 RR: Receiver Report RTCP Packet
		
			0                   1                   2                   3
			0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
			+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			header |V=2|P|    RC   |   PT=RR=201   |             length     |
			+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			|                     SSRC of packet sender                     |
			+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	report  |                 SSRC_1 (SSRC of first source)                 |
	block   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	  1     | fraction lost |       cumulative number of packets lost       |
			+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			|           extended highest sequence number received           |
			+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			|                      interarrival jitter                      |
			+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			|                         last SR (LSR)                         |
			+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			|                   delay since last SR (DLSR)                  |
			+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	report  |                 SSRC_2 (SSRC of second source)                |
	block   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	  2     :                               ...                             :
			+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
			|                  profile-specific extensions                  |
			+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

*/

public class ReceiverReport extends RtcpBasePacket 
{
	private Integer ssrc;
	
    private List<ReportBlock> reportBlocks;
    
    public ReceiverReport(RtcpHeader header, Integer ssrc)
    {
        super(header);
    }
    

	public Integer getSSRC()
	{
		return this.ssrc;
	}
	
	public void setSSRC(Integer value)
	{       
        this.ssrc = value;
	}
    
    public List<ReportBlock> getReportBlocks()
    {
        return reportBlocks; 
    }
    
    public void setReportBlocks(List<ReportBlock> value) 
    {
        this.reportBlocks = value;
    }

}
    