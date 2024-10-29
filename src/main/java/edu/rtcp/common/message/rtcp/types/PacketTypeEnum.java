package edu.rtcp.common.message.rtcp.types;

import java.util.HashMap;
import java.util.Map;



public enum PacketTypeEnum {
	SENDER_REPORT(200),
	RECEIVER_REPORT(201),
	SOURCE_DESCRIPTION(202),
	BYE(203),
	APP(204);

	private static final Map<Integer, PacketTypeEnum> intToTypeMap = new HashMap<>();

	static {
	    for (PacketTypeEnum type : PacketTypeEnum.values()) {
	    	intToTypeMap.put(type.value, type);
	    }
	}

	public static PacketTypeEnum fromInt(int value) {
	    return intToTypeMap.get(value);
	}
	
	private final int value;
	
	PacketTypeEnum(int value) {
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}
