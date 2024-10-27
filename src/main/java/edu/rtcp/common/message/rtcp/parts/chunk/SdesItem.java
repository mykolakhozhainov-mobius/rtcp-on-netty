package edu.rtcp.common.message.rtcp.parts.chunk;

import edu.rtcp.common.message.rtcp.exception.RtcpException;
import edu.rtcp.common.message.rtcp.types.ItemsTypeEnum;

/*
  6.5.1 CNAME: Canonical End-Point Identifier SDES Item

    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    
   6.5.1 CNAME: Canonical End-Point Identifier SDES Item
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |    CNAME=1    |     length    | user and domain name        ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
   6.5.2 NAME: User Name SDES Item
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |     NAME=2    |     length    | common name of source       ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
   6.5.3 EMAIL: Electronic Mail Address SDES Item
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |    EMAIL=3    |     length    | email address of source     ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
   6.5.4 PHONE: Phone Number SDES Item
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |    PHONE=4    |     length    | phone number of source      ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
   6.5.5 LOC: Geographic User Location SDES Item
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |     LOC=5     |     length    | geographic location of site ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
   6.5.6 TOOL: Application or Tool Name SDES Item
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |     TOOL=6    |     length    |name/version of source appl. ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
   6.5.7 NOTE: Notice/Status SDES Item
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |     NOTE=7    |     length    | note about the source       ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
   6.5.8 PRIV: Private Extensions SDES Item
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |     PRIV=8    |     length    | prefix length |prefix string...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   ...             |                  value string               ...
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   
 */

public class SdesItem {
    private ItemsTypeEnum itemsType;
    private int length;
    private String data;
    private int prefixLength;
    private String prefix;
    
    public SdesItem(ItemsTypeEnum itemsType, int length, String data)
    {
        this.itemsType = itemsType;
        this.length = length;
        this.data = data;
    }
    
    public SdesItem(ItemsTypeEnum itemsType, int length, int prefixLength, String prefix, String data) {
        this.itemsType = itemsType;
        this.length = length;
        this.prefixLength = prefixLength;
        this.data = data;
        this.prefix = prefix;
    }

    public ItemsTypeEnum getType() 
    {
        return itemsType;
    }

    public void setType(ItemsTypeEnum value) throws RtcpException {
    	if (value == null) throw new RtcpException("Type cannot be null");
        
        this.itemsType = value;
    }
    
    public int getLength()
    {
        return length;
    }

    public void setLength(int value) 
    {
        this.length = value;
    }
    
    public String getData() 
    {
        return data;
    }

    public void setData(String value) throws RtcpException {
    	if (value == null) throw new RtcpException("Data cannot be null");
        
        this.data = value;
    }
    
    public int getPrefixLength() {
        return prefixLength;
    }
    
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String value) {
        this.prefix = value;
    }
    
}