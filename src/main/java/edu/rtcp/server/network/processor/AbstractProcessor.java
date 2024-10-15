package edu.rtcp.server.network.processor;

import edu.rtcp.server.network.executor.MessageExecutor;
import edu.rtcp.RtcpStack;

public abstract class AbstractProcessor {
    public MessageExecutor executor = new MessageExecutor();
    protected RtcpStack stack;
    protected int port;

    public abstract void start();

    public void run(int workersNumber, int taskInterval) {
        this.start();

        executor.start(workersNumber, taskInterval);
    }
}
