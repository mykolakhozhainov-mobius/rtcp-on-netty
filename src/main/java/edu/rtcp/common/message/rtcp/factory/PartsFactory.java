package edu.rtcp.common.message.rtcp.factory;

import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.common.message.rtcp.parts.chunk.Chunk;
import edu.rtcp.common.message.rtcp.parts.chunk.SdesItem;

import java.util.List;

public class PartsFactory {
    public ReportBlock createReportBlock(
            int ssrc,
            byte fractionLost,
            int cumulativePacketsLost,
            int extendedHighestSeqNumber,
            int interarrivalJitter,
            int lastSenderReport,
            int delaySinceLastSenderReport
    ) {
        return new ReportBlock(
                ssrc,
                fractionLost,
                cumulativePacketsLost,
                extendedHighestSeqNumber,
                interarrivalJitter,
                lastSenderReport,
                delaySinceLastSenderReport
        );
    }
    
    public Chunk createChunk(int ssrc, List<SdesItem> items)
    {
        return new Chunk(ssrc,items);
    }
}
