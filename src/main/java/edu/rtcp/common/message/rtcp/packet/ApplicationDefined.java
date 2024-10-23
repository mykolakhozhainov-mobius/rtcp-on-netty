package edu.rtcp.common.message.rtcp.packet;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.header.RtcpHeader;

/*
		6.7 APP: Application-Defined RTCP Packet
		
		0                   1                   2                   3
		0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		|V=2|P| subtype |   PT=APP=204  |             length            |
		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		|                           SSRC/CSRC                           |
		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		|                          name (ASCII)                         |
		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		|                   application-dependent data                ...
		+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

*/

public class ApplicationDefined extends RtcpBasePacket {
	public String name;
	public Integer applicationDependentData;   

	public ApplicationDefined(
			RtcpHeader header,
			int ssrc,
			String name,
			int applicationDependentData
	) {
		super(header);

		this.ssrc = ssrc;
		this.name = name;
		this.applicationDependentData = applicationDependentData;
	}

	
	public String getName() 
	{
		return name;
	}
	
	public Integer getApplicationDependentData()
	{
		return applicationDependentData;
	}
}