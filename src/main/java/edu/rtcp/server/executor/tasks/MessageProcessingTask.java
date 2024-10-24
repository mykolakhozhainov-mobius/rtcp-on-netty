package edu.rtcp.server.executor.tasks;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;

public class MessageProcessingTask extends MessageTask {
    private final RtcpStack stack;

    public MessageProcessingTask(RtcpBasePacket message, RtcpStack stack) {
        this.message = message;
        this.stack = stack;
    }
    
    @Override
    public void execute() {
        System.out.println("[PROCESSING-TASK] Executing incoming message");

        this.stack.getProvider().onMessage(this.message, new AsyncCallback() {
            @Override
            public void onSuccess() {
                System.out.println("[TASK] Completely processed");
            }

            @Override
            public void onError(Exception e) {
                System.out.println("[TASK] Exception handled:");
                System.out.println(e.getMessage());
            }
        });
    }
}
