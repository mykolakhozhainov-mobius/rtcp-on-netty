package edu.netty.common.message.rtcp.header;

import edu.netty.common.message.rtcp.types.PacketTypeEnum;

public class RtcpHeader 
{
	private Short version;
	private Boolean isPadding;
	private Short itemCount;
	private PacketTypeEnum packetType;
	private Integer length;
	private Integer ssrc;
	
	private boolean ssrcAllowed = true;
	
	public RtcpHeader(Short version, Boolean isPadding, Short itemCount, PacketTypeEnum packetType, Integer length, Integer ssrc)
	{
		this.isPadding = isPadding;
		this.version = version;
		
		setItemCount(itemCount);
		setPacketType(packetType);
	    setLength(length);
	    setSSRC(ssrc);  
	 }
	
	public void setSSRCAllowed(boolean allowed) 
	{
		this.ssrcAllowed = allowed;
	}
	
	public int getVersion() 
	{
		return this.version;
	}
	
	public void setVersion(Short value) 
	{
		this.version = value;
	}
	
	public Boolean getIsPadding()
	{
		return this.isPadding;
	}
	
	public void setIsPadding(Boolean value)
	{
		this.isPadding = value;
	}
	
	public int getItemCount() 
	{
		return this.itemCount;
	}
	
	public void setItemCount(Short value) 
	{
		this.itemCount = value;
	}
	
	public PacketTypeEnum getPacketType() 
	{
		return this.packetType;
	}
	
	public void setPacketType(PacketTypeEnum value) 
	{
		this.packetType = value;
	}
	
	public int getLength() 
	{
		return this.length;
	}
	
	public void setLength(Integer value) 
	{
		this.length = value;
	}
	
	public Integer getSSRC()
	{
		return this.ssrc;
	}
	
	public void setSSRC(Integer value)
	{
		//TODO Зробити виключення та продумати валідацію, а можливо й щось типу DiameterOrder

			if(!ssrcAllowed && ssrc!=null)
				
            throw new UnsupportedOperationException("SSRC is not allowed for this packet.");
            
            this.ssrc = value;
	}
}