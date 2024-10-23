package edu.rtcp.server.network.processor;

import edu.rtcp.RtcpStack;

public abstract class AbstractProcessor {
    protected RtcpStack stack;
    protected int port;

    public abstract void start();
}
