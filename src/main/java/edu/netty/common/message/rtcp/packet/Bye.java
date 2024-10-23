package edu.netty.common.message.rtcp.packet;

import edu.netty.common.message.rtcp.header.RtcpBasePacket;
import edu.netty.common.message.rtcp.header.RtcpHeader;

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

public class Bye extends RtcpBasePacket 
{
	private Integer ssrc;
	
	private Integer lengthOfReason;
	
    private String reason;

    public Bye(RtcpHeader header, Integer ssrc)
    {
        super(header);
        this.ssrc = ssrc;
    }
    

	public Integer getSSRC()
	{
		return this.ssrc;
	}
	
	public void setSSRC(Integer value)
	{       
        this.ssrc = value;
	}
	
	public Integer getLengthOfReason()
	{
		if(lengthOfReason==null)
			return null;
		
		return this.lengthOfReason;
	}
	
	public void setLengthOfReason(Integer value)
	{       
		if(value == null)
		this.lengthOfReason = null;
			
        this.lengthOfReason = value;
	}

    public String getReason() 
    {
    	if(reason==null)
			return null;
		
		return this.reason;
        
    }

    public void setReason(String value) 
    {
    	if(value == null)
    	this.reason = null;
    			
        this.reason = value;
    }
}
	
	
	
