package edu.netty.common.message.rtcp.packet;

import edu.netty.common.message.rtcp.header.RtcpBasePacket;
import edu.netty.common.message.rtcp.header.RtcpHeader;
import edu.netty.common.message.rtcp.parts.chunk.Chunk;

/*
 	6.5 SDES: Source Description RTCP Packet
 
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

public class SourceDescription extends RtcpBasePacket 
{
	public Chunk chunk;

	public SourceDescription(RtcpHeader header) 
	{
		super(header);
		header.setSSRCAllowed(false);
	}
	
	public Chunk getChunk()
	{
		return chunk;
	}
	
	public void setChunk(Chunk value)
	{
		this.chunk = value;
	}
	
}