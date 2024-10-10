package edu.netty.common.message.rtcp.factory;

import edu.netty.common.message.rtcp.parts.chunk.SdesItem;
import edu.netty.common.message.rtcp.types.ItemsTypeEnum;

public class SdesItemFactory 
{
    public static SdesItem createCname(String value) 
    {
        return new SdesItem(ItemsTypeEnum.CNAME, value);
    }

    public static SdesItem createName(String value) 
    {
        return new SdesItem(ItemsTypeEnum.NAME, value);
    }

    public static SdesItem createEmail(String value) 
    {
        return new SdesItem(ItemsTypeEnum.EMAIL, value);
    }

    public static SdesItem createPhone(String value) 
    {
        return new SdesItem(ItemsTypeEnum.PHONE, value);
    }

    public static SdesItem createLocation(String value) 
    {
        return new SdesItem(ItemsTypeEnum.LOC, value);
    }

    public static SdesItem createTool(String value) 
    {
        return new SdesItem(ItemsTypeEnum.TOOL, value);
    }

    public static SdesItem createNote(String value) 
    {
        return new SdesItem(ItemsTypeEnum.NOTE, value);
    }

    public static SdesItem createPriv(String value, String prefix) 
    {
        return new SdesItem(ItemsTypeEnum.PRIV, value, prefix);
    }
}
