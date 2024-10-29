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

public class ReportBlock {
    private int ssrc; 
    private byte fractionLost; 
    private int cumulativePacketsLost; 
    private int extendedHighestSeqNumber; 
    private int interarrivalJitter; 
    private int lastSenderReport; 
    private int delaySinceLastSenderReport; 

    public ReportBlock(
            int ssrc,
            byte fractionLost,
            int cumulativePacketsLost,
            int extendedHighestSeqNumber,
            int interarrivalJitter,
            int lastSenderReport,
            int delaySinceLastSenderReport
    ) {
        this.ssrc = ssrc;
        this.fractionLost = fractionLost;
        this.cumulativePacketsLost = cumulativePacketsLost;
        this.extendedHighestSeqNumber = extendedHighestSeqNumber;
        this.interarrivalJitter = interarrivalJitter;
        this.lastSenderReport = lastSenderReport;
        this.delaySinceLastSenderReport = delaySinceLastSenderReport;
    }

   
    public int  getSsrc() 
    {
        return ssrc;
    }

    public void setSsrc(int value) 
    {
        this.ssrc = value;
    }

    public byte getFractionLost() {
        return fractionLost;
    }

    public void setFractionLost(byte value) 
    {
        this.fractionLost = value;
    }

    public int getCumulativePacketsLost() 
    {
        return cumulativePacketsLost;
    }

    public void setCumulativePacketsLost(int value) 
    {
        this.cumulativePacketsLost = value;
    }

    public int getExtendedHighestSeqNumber() 
    {
        return extendedHighestSeqNumber;
    }

    public void setExtendedHighestSeqNumber(int value) 
    {
        this.extendedHighestSeqNumber = value;
    }

    public int getInterarrivalJitter() 
    {
        return interarrivalJitter;
    }

    public void setInterarrivalJitter(int value) 
    {
        this.interarrivalJitter = value;
    }

    public int getLastSenderReport()
    {
        return lastSenderReport;
    }

    public void setLastSenderReport(int value) 
    {
        this.lastSenderReport = value;
    }

    public int getDelaySinceLastSenderReport() 
    {
        return delaySinceLastSenderReport;
    }

    public void setDelaySinceLastSenderReport(int value) {
    	
        this.delaySinceLastSenderReport = value;
    }
}