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
	private Integer ssrc;
	
	public String name;
	
	public Integer applicationDependentData;   

	public ApplicationDefined(RtcpHeader header, Integer ssrc, String name, Integer applicationDependentData) 
	{
		super(header);
		
		setSSRC(ssrc);
		setName(name);
		setApplicationDependentData(applicationDependentData);
	}
	
	public Integer getSSRC()
	{
		return this.ssrc;
	}
	
	public void setSSRC(Integer value)
	{       
        this.ssrc = value;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String value) 
	{
		this.name = value;
	}
	
	public Integer getApplicationDependentData()
	{
		return applicationDependentData;
	}
	
	public void setApplicationDependentData(Integer value) 
	{
		this.applicationDependentData = value;
	}

}