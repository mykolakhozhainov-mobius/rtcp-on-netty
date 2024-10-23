package edu.rtcp.common.message.rtcp.packet;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.header.RtcpHeader;

/*
 
 	   6.6 BYE: Goodbye RTCP Packet
 	   
	  0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |V=2|P|    SC   |   PT=BYE=203  |             length            |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                           SSRC/CSRC                           |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      :                              ...                              :
      +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
(opt) |     length    |               reason for leaving            ...
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      
*/

public class Bye extends RtcpBasePacket {
	private int lengthOfReason;
    private String reason;

    public Bye(RtcpHeader header, Integer ssrc) {
        super(header);
        this.ssrc = ssrc;
    }

	public int getLengthOfReason() {
		return this.lengthOfReason;
	}
	
	public void setLengthOfReason(int value) {
        this.lengthOfReason = value;
	}

    public String getReason() {
		return this.reason;
    }

    public void setReason(String value) {
        this.reason = value;
    }
}
	
	
	
