package edu.netty.server.task;

import com.mobius.software.common.dal.timers.Task;
import edu.netty.common.SimpleMessage;
import edu.netty.server.channel.ProcessingChannel;

public class MessageProcessingTask implements Task {
    private final SimpleMessage data;
    private final ProcessingChannel channel;

    public MessageProcessingTask(ProcessingChannel channel, SimpleMessage msg) {
        super();
        this.data = msg;
        this.channel = channel;
    }
    
    @Override
    public void execute() {
        System.out.println("[TASK] Starting executing " + channel.getChannel().id());
        channel.process(data);
    }

    @Override
    public long getStartTime() {
        return System.currentTimeMillis();
    }

    public String getId() {
        return "MessageTask-" + System.currentTimeMillis();
    }
}
