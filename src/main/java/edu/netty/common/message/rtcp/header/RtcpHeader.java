package edu.netty.common.message.rtcp.header;

import edu.netty.common.message.rtcp.types.PacketTypeEnum;

public class RtcpHeader 
{
	private Byte version;
	private Boolean isPadding;
	private Byte itemCount;
	private PacketTypeEnum packetType;
	private Short length;

	
	public RtcpHeader(Byte version, Boolean isPadding, Byte itemCount, PacketTypeEnum packetType, Short length)
	{
		this.isPadding = isPadding;
		this.version = version;
		
		setItemCount(itemCount);
		setPacketType(packetType);
	    setLength(length);
	 }
	
	public int getVersion() 
	{
		return this.version;
	}
	
	public void setVersion(Byte value) 
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
	
	public Byte getItemCount() 
	{
		return this.itemCount;
	}
	
	public void setItemCount(Byte value) 
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
	
	public void setLength(Short value) 
	{
		this.length = value;
	}
}