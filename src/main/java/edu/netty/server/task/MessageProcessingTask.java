package edu.netty.server.task;

import edu.netty.common.message.Message;
import edu.netty.server.channel.AbstractChannel;

public class MessageProcessingTask implements IdentifiedTask {
    private final Message message;
    private final AbstractChannel channel;

    public MessageProcessingTask(AbstractChannel channel, Message message) {
        this.message = message;
        this.channel = channel;
    }
    
    @Override
    public void execute() {
        System.out.println("[TASK] Starting executing [" + this.message.content + "] via channel " + channel.getChannel().id());
        channel.process(message);
    }

    @Override
    public long getStartTime() {
        return System.currentTimeMillis();
    }

    public String getId() {
        if (this.channel.isSessioned(message)) {
            return this.message.sessionId.toString();
        }

        return String.valueOf(System.currentTimeMillis());
    }
}
