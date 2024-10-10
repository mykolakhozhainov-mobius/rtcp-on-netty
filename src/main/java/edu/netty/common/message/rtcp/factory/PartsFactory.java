package edu.netty.common.message.rtcp.factory;

import java.util.List;

import edu.netty.common.message.rtcp.parts.ReportBlock;
import edu.netty.common.message.rtcp.parts.chunk.Chunk;
import edu.netty.common.message.rtcp.parts.chunk.SdesItem;

public class PartsFactory 
{

    public ReportBlock createReportBlock(Integer ssrc, Short fractionLost, Integer cumulativePacketsLost, Integer extendedHighestSeqNumber, Integer interarrivalJitter, Integer lastSenderReport, Integer delaySinceLastSenderReport)
    {
        return new ReportBlock(ssrc, fractionLost, cumulativePacketsLost, extendedHighestSeqNumber, interarrivalJitter, lastSenderReport, delaySinceLastSenderReport);
    }
    
    public Chunk createChunk(Integer ssrc,List<SdesItem> items) 
    {
        return new Chunk(ssrc,items);
    }
}
