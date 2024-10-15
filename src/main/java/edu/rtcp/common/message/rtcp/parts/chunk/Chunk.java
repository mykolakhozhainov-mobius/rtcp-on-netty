package edu.rtcp.common.message.rtcp.parts.chunk;

import java.util.List;
/*
  
	 	   0                   1                   2                   3
		   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
		  +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
chunk     |                          SSRC/CSRC_1                          |
	      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	      |                           SDES items                          |
	      |                              ...                              |
	      +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 */

public class Chunk 
{
    private Integer ssrc;
    private List<SdesItem> items;

    public Chunk(Integer ssrc,List<SdesItem> items) 
    {
    	this.ssrc = ssrc;
    	this.items = items;
    }

    public Integer getSsrc() 
    {
        return ssrc;
    }

    public void setSsrc(Integer value) 
    {
        this.ssrc = value;
    }
    
    public List<SdesItem> getItems() 
    {
        return items;
    }
  
    public void setItems(List<SdesItem> value) 
    {
    	this.items = value;
    }
}
