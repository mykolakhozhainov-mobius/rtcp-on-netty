package edu.rtcp.server.network.executor.tasks;

import com.mobius.software.common.dal.timers.Task;
import edu.rtcp.common.message.Message;

public abstract class MessageTask implements Task {
    protected Message message;

    @Override
    public long getStartTime() {
        return System.currentTimeMillis();
    }

    public String getId() {
        if (this.message.sessionId != null) {
            return this.message.sessionId.toString();
        }

        return String.valueOf(System.currentTimeMillis());
    }

    public abstract void execute();
}
