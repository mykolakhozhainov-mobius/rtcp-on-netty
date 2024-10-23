package edu.rtcp.server.executor.tasks;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;

public class MessageOutgoingTask extends MessageTask {
    private final RtcpStack stack;
    private final int port;
    private final AsyncCallback callback;

    public MessageOutgoingTask(RtcpBasePacket message, int port, RtcpStack stack, AsyncCallback callback) {
        this.message = message;
        this.stack = stack;
        this.port = port;
        this.callback = callback;
    }

    @Override
    public void execute() {
        this.stack.getNetworkManager().sendMessage(message, port, callback);
    }
}
