package edu.netty.common.message.rtcp.types;

import java.util.HashMap;
import java.util.Map;

public enum ItemsTypeEnum 
{
    CNAME(1), NAME(2), EMAIL(3), PHONE(4), LOC(5), TOOL(6), NOTE(7), PRIV(8);

    private static final Map<Integer, ItemsTypeEnum> intToTypeMap = new HashMap<>();

    static 
    {
        for (ItemsTypeEnum type : ItemsTypeEnum.values()) 
        {
            intToTypeMap.put(type.value, type);
        }
    }

    public static ItemsTypeEnum fromInt(Integer value) 
    {
        return intToTypeMap.get(value);
    }

    private int value;

    ItemsTypeEnum(int value) 
    {
        this.value = value;
    }

    public int getValue() 
    {
        return value;
    }
}
