package edu.rtcp.server.executor.tasks;

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

        this.stack.getNetworkManager()
                .getNetworkListener()
                .onMessage(
                        this.message,
                        this.stack.getProvider().getSessionStorage().get(message.sessionId),
                        new AsyncCallback() {
            @Override
            public void onSuccess() {
                System.out.println("[TASK] Message is gone through network listener");
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
