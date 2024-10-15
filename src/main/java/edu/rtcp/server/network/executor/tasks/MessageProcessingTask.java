package edu.rtcp.server.network.executor.tasks;

import edu.rtcp.common.message.Message;
import edu.rtcp.RtcpStack;
import edu.rtcp.server.callback.AsyncCallback;

public class MessageProcessingTask extends MessageTask {
    private final RtcpStack stack;

    public MessageProcessingTask(Message message, RtcpStack stack) {
        this.message = message;
        this.stack = stack;
    }
    
    @Override
    public void execute() {
        System.out.println("[TASK] Starting executing [" + this.message.content + "]");

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
