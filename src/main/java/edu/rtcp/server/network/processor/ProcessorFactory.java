package edu.rtcp.server.network.processor;

import edu.rtcp.RtcpStack;
import edu.rtcp.server.network.processor.transport.DatagramProcessor;
import edu.rtcp.server.network.processor.transport.StreamProcessor;

public class ProcessorFactory {
    public AbstractProcessor getDatagramProcessor(int port, RtcpStack stack) {
        return new DatagramProcessor(port, stack);
    }

    public AbstractProcessor getStreamProcessor(int port, RtcpStack stack) {
        return new StreamProcessor(port, stack);
    }
}
