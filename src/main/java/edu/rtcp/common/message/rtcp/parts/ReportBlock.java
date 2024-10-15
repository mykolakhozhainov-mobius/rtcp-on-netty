package edu.rtcp.common.message.rtcp.parts;

/*
 		0                   1                   2                   3
        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	   +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
report |                 SSRC_1 (SSRC of first source)                 |
block  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       | fraction lost |       cumulative number of packets lost       |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |           extended highest sequence number received           |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                      interarrival jitter                      |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                         last SR (LSR)                         |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                   delay since last SR (DLSR)                  |
       +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
*/

public class ReportBlock
{
    private Integer ssrc; 
    private Short fractionLost; 
    private Integer cumulativePacketsLost; 
    private Integer extendedHighestSeqNumber; 
    private Integer interarrivalJitter; 
    private Integer lastSenderReport; 
    private Integer delaySinceLastSenderReport; 

    public ReportBlock(Integer ssrc, Short fractionLost, Integer cumulativePacketsLost, Integer extendedHighestSeqNumber, Integer interarrivalJitter, Integer lastSenderReport, Integer delaySinceLastSenderReport) 
    {
        this.ssrc = ssrc;
        this.fractionLost = fractionLost;
        this.cumulativePacketsLost = cumulativePacketsLost;
        this.extendedHighestSeqNumber = extendedHighestSeqNumber;
        this.interarrivalJitter = interarrivalJitter;
        this.lastSenderReport = lastSenderReport;
        this.delaySinceLastSenderReport = delaySinceLastSenderReport;
    }

   
    public long getSsrc() 
    {
        return ssrc;
    }

    public void setSsrc(Integer value) 
    {
        this.ssrc = value;
    }

    public int getFractionLost() {
        return fractionLost;
    }

    public void setFractionLost(Short value) 
    {
        this.fractionLost = value;
    }

    public int getCumulativePacketsLost() 
    {
        return cumulativePacketsLost;
    }

    public void setCumulativePacketsLost(Integer value) 
    {
        this.cumulativePacketsLost = value;
    }

    public long getExtendedHighestSeqNumber() 
    {
        return extendedHighestSeqNumber;
    }

    public void setExtendedHighestSeqNumber(Integer value) 
    {
        this.extendedHighestSeqNumber = value;
    }

    public long getInterarrivalJitter() 
    {
        return interarrivalJitter;
    }

    public void setInterarrivalJitter(Integer value) 
    {
        this.interarrivalJitter = value;
    }

    public long getLastSenderReport()
    {
        return lastSenderReport;
    }

    public void setLastSenderReport(Integer value) 
    {
        this.lastSenderReport = value;
    }

    public long getDelaySinceLastSenderReport() 
    {
        return delaySinceLastSenderReport;
    }

    public void setDelaySinceLastSenderReport(Integer value) {
    	
        this.delaySinceLastSenderReport = value;
    }
}