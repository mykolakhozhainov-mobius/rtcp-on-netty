package edu.rtcp.common.message.rtcp.factory;

import edu.rtcp.common.message.rtcp.parts.chunk.SdesItem;
import edu.rtcp.common.message.rtcp.types.ItemsTypeEnum;

public class SdesItemFactory {
    public static SdesItem createCname(int length, String data) {
        return new SdesItem(ItemsTypeEnum.CNAME, length, data);
    }

    public static SdesItem createName(int length, String data) {
        return new SdesItem(ItemsTypeEnum.NAME, length, data);
    }

    public static SdesItem createEmail(int length,String data) {
        return new SdesItem(ItemsTypeEnum.EMAIL, length, data);
    }

    public static SdesItem createPhone(int length, String data) {
        return new SdesItem(ItemsTypeEnum.PHONE, length, data);
    }

    public static SdesItem createLocation(int length, String data) {
        return new SdesItem(ItemsTypeEnum.LOC, length, data);
    }

    public static SdesItem createTool(int length, String data) {
        return new SdesItem(ItemsTypeEnum.TOOL, length, data);
    }

    public static SdesItem createNote(int length, String data) {
        return new SdesItem(ItemsTypeEnum.NOTE, length, data);
    }

    public static SdesItem createPriv(int length, String data, int prefixLength, String prefix) {
        return new SdesItem(ItemsTypeEnum.PRIV, length, prefixLength, data, prefix);
    }
}
