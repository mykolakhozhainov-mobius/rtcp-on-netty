package edu.rtcp.server.callback.header;

import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;

public class RtcpHeader {
	private final byte version;
	private final boolean isPadding;
	private final byte itemCount;
	private final PacketTypeEnum packetType;
	private short length;

	public RtcpHeader(
			byte version,
			boolean isPadding,
			byte itemCount,
			PacketTypeEnum packetType,
			short length
	) {
		this.isPadding = isPadding;
		this.version = version;

		this.itemCount = itemCount;
		this.packetType = packetType;
		this.length = length;
	 }
	
	public int getVersion() 
	{
		return this.version;
	}

	public boolean getIsPadding()
	{
		return this.isPadding;
	}

	public byte getItemCount()
	{
		return this.itemCount;
	}

	public PacketTypeEnum getPacketType() 
	{
		return this.packetType;
	}

	public int getLength() 
	{
		return this.length;
	}
	
	public void setLength(short value)
	{
		this.length = value;
	}
}