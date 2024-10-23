package edu.rtcp.common.message.rtcp.factory;

import edu.rtcp.common.message.rtcp.parts.chunk.SdesItem;
import edu.rtcp.common.message.rtcp.types.ItemsTypeEnum;

public class SdesItemFactory 
{
    public static SdesItem createCname(Integer length, String data) 
    {
        return new SdesItem(ItemsTypeEnum.CNAME, length, data);
    }

    public static SdesItem createName(Integer length, String data) 
    {
        return new SdesItem(ItemsTypeEnum.NAME, length, data);
    }

    public static SdesItem createEmail(Integer length,String data) 
    {
        return new SdesItem(ItemsTypeEnum.EMAIL, length, data);
    }

    public static SdesItem createPhone(Integer length, String data) 
    {
        return new SdesItem(ItemsTypeEnum.PHONE, length, data);
    }

    public static SdesItem createLocation(Integer length,String data) 
    {
        return new SdesItem(ItemsTypeEnum.LOC, length, data);
    }

    public static SdesItem createTool(Integer length,String data) 
    {
        return new SdesItem(ItemsTypeEnum.TOOL, length, data);
    }

    public static SdesItem createNote(Integer length,String data) 
    {
        return new SdesItem(ItemsTypeEnum.NOTE, length, data);
    }

    public static SdesItem createPriv(Integer length,String data, Integer prefixLength, String prefix) 
    {
        return new SdesItem(ItemsTypeEnum.PRIV, length, prefixLength, data, prefix);
    }
}
