package edu.netty.common.message.rtcp.factory;

import edu.netty.common.message.rtcp.parts.ReportBlock;
import edu.netty.common.message.rtcp.parts.chunk.Chunk;
import edu.netty.common.message.rtcp.parts.chunk.SdesItem;

import java.util.List;

public class PartsFactory 
{

    public ReportBlock createReportBlock(Integer ssrc, Byte fractionLost, Integer cumulativePacketsLost, Integer extendedHighestSeqNumber, Integer interarrivalJitter, Integer lastSenderReport, Integer delaySinceLastSenderReport)
    {
        return new ReportBlock(ssrc, fractionLost, cumulativePacketsLost, extendedHighestSeqNumber, interarrivalJitter, lastSenderReport, delaySinceLastSenderReport);
    }
    
    public Chunk createChunk(Integer ssrc,List<SdesItem> items) 
    {
        return new Chunk(ssrc,items);
    }
}
