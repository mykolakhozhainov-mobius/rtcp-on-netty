package edu.netty.task;

import com.mobius.software.common.dal.timers.Task;
import edu.netty.server.ProcessingChannel;

public class MessageProcessingTask implements Task {
    private final String data;
    private final ProcessingChannel channel;

    public MessageProcessingTask(ProcessingChannel channel, Object msg) {
        super();
        this.data = msg.toString();
        this.channel = channel;
    }
    
    @Override
    public void execute() {
        System.out.println("Task executing:");
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
