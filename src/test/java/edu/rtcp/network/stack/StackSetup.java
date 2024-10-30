package edu.rtcp.network.stack;

import edu.rtcp.RtcpStack;

public interface StackSetup {
    RtcpStack setupServer() throws Exception;
    RtcpStack setupClient() throws Exception;

    RtcpStack getServerStack();
    RtcpStack getClientStack();

    int getClientPort();
    int getServerPort();
}
