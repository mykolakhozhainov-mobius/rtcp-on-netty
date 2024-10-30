package edu.rtcp.server.executor.tasks;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;

public class MessageProcessingTask extends MessageTask {
    public static Logger logger = LogManager.getLogger(MessageProcessingTask.class);
    private InetSocketAddress address;

    private final RtcpStack stack;

    public MessageProcessingTask(RtcpBasePacket message, InetSocketAddress address, RtcpStack stack) {
        super(message);
        this.address = address;

        this.stack = stack;
    }
    
    @Override
    public void execute() {
        this.stack.getProvider().onMessage(this.message, address, new AsyncCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onError(Exception e) {
                logger.error(e);
            }
        });
    }
}
