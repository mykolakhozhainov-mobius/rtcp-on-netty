package edu.rtcp.server.executor.tasks;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageProcessingTask extends MessageTask {
    public static Logger logger = LogManager.getLogger(MessageProcessingTask.class);

    private final RtcpStack stack;

    public MessageProcessingTask(RtcpBasePacket message, RtcpStack stack) {
        super(message);

        this.stack = stack;
    }
    
    @Override
    public void execute() {
        this.stack.getProvider().onMessage(this.message, new AsyncCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onError(Exception e) {
                logger.error(e);
            }
        });
    }
}
