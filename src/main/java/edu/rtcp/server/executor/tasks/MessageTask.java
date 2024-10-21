package edu.rtcp.server.executor.tasks;

import com.mobius.software.common.dal.timers.Task;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;

public abstract class MessageTask implements Task {
    protected RtcpBasePacket message;

    @Override
    public long getStartTime() {
        return System.currentTimeMillis();
    }

    public String getId() {
        if (this.message.getHeader().getSSRC() != null) {
            return this.message.getHeader().getSSRC().toString();
        }

        return String.valueOf(System.currentTimeMillis());
    }

    public abstract void execute();
}
