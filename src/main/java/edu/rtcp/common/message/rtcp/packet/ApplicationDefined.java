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

public class ApplicationDefined extends RtcpBasePacket 
{
	public String name;
	
	public Long applicationDependentData;   

	public ApplicationDefined(RtcpHeader header, String name, Long applicationDependentData) 
	{
		super(header);
		
		setName(name);
		setApplicationDependentData(applicationDependentData);
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String value) 
	{
		this.name = value;
	}
	
	public Long getApplicationDependentData()
	{
		return applicationDependentData;
	}
	
	public void setApplicationDependentData(Long value) 
	{
		this.applicationDependentData = value;
	}

}