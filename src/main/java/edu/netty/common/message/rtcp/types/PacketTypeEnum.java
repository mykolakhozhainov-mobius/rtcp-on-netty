package edu.netty.common.message.rtcp.types;

import java.util.HashMap;
import java.util.Map;



public enum PacketTypeEnum 
{
	SENDER_REPORT(200),RECEIVER_REPORT(201),SOURCE_DESCRIPTION(202),BYE(203),APP(204);

	private static final Map<Integer, PacketTypeEnum> intToTypeMap = new HashMap<Integer, PacketTypeEnum>();
	static 
	{
	    for (PacketTypeEnum type : PacketTypeEnum.values()) 
	    {
	    	intToTypeMap.put(type.value, type);
	    }
	}

	public static PacketTypeEnum fromInt(Integer value) 
	{
		PacketTypeEnum type = intToTypeMap.get(value);
	    return type;
	}
	
	private int value;
	
	private PacketTypeEnum(int value)
	{
		this.value=value;
	}
	
	public int getValue()
	{
		return value;
	}
}
