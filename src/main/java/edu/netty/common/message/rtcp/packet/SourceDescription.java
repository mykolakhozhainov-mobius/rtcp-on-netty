package edu.netty.common.message.rtcp.packet;

import edu.netty.common.message.rtcp.header.RTCPBasePacket;
import edu.netty.common.message.rtcp.header.RTCPHeader;

/*
	        0                   1                   2                   3
	        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	header |V=2|P|    SC   |  PT=SDES=202  |             length            |
	       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	chunk  |                          SSRC/CSRC_1                          |
	  1    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                           SDES items                          |
	       |                              ...                              |
	       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
	chunk  |                          SSRC/CSRC_2                          |
	  2    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	       |                           SDES items                          |
	       |                              ...                              |
	       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
*/

public class SourceDescription extends RTCPBasePacket 
{

	public SourceDescription(RTCPHeader header) 
	{
		super(header);
		header.setSSRCAllowed(false);
	}
	
}