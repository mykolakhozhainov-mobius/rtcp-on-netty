package edu.netty.common.message.rtcp.parts;

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
    private Byte fractionLost; 
    private Integer cumulativePacketsLost; 
    private Integer extendedHighestSeqNumber; 
    private Integer interarrivalJitter; 
    private Integer lastSenderReport; 
    private Integer delaySinceLastSenderReport; 

    public ReportBlock(Integer ssrc, Byte fractionLost, Integer cumulativePacketsLost, Integer extendedHighestSeqNumber, Integer interarrivalJitter, Integer lastSenderReport, Integer delaySinceLastSenderReport) 
    {
        this.ssrc = ssrc;
        this.fractionLost = fractionLost;
        this.cumulativePacketsLost = cumulativePacketsLost;
        this.extendedHighestSeqNumber = extendedHighestSeqNumber;
        this.interarrivalJitter = interarrivalJitter;
        this.lastSenderReport = lastSenderReport;
        this.delaySinceLastSenderReport = delaySinceLastSenderReport;
    }

   
    public Integer  getSsrc() 
    {
        return ssrc;
    }

    public void setSsrc(Integer value) 
    {
        this.ssrc = value;
    }

    public Byte getFractionLost() {
        return fractionLost;
    }

    public void setFractionLost(Byte value) 
    {
        this.fractionLost = value;
    }

    public Integer getCumulativePacketsLost() 
    {
        return cumulativePacketsLost;
    }

    public void setCumulativePacketsLost(Integer value) 
    {
        this.cumulativePacketsLost = value;
    }

    public Integer getExtendedHighestSeqNumber() 
    {
        return extendedHighestSeqNumber;
    }

    public void setExtendedHighestSeqNumber(Integer value) 
    {
        this.extendedHighestSeqNumber = value;
    }

    public Integer getInterarrivalJitter() 
    {
        return interarrivalJitter;
    }

    public void setInterarrivalJitter(Integer value) 
    {
        this.interarrivalJitter = value;
    }

    public Integer getLastSenderReport()
    {
        return lastSenderReport;
    }

    public void setLastSenderReport(Integer value) 
    {
        this.lastSenderReport = value;
    }

    public Integer getDelaySinceLastSenderReport() 
    {
        return delaySinceLastSenderReport;
    }

    public void setDelaySinceLastSenderReport(Integer value) {
    	
        this.delaySinceLastSenderReport = value;
    }
}