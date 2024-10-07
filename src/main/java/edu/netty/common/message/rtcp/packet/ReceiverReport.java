package edu.netty.common.message.rtcp.packet;

import java.util.List;

import edu.netty.common.message.rtcp.header.RTCPBasePacket;
import edu.netty.common.message.rtcp.header.RTCPHeader;
import edu.netty.common.message.rtcp.parts.ReportBlock;


/*
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

public class ReceiverReport extends RTCPBasePacket 
{
	private List<ReportBlock> reportBlock;

	public ReceiverReport(RTCPHeader header) 
	{
		super(header);
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

}