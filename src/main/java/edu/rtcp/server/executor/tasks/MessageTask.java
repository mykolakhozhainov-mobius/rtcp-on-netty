package edu.rtcp.server.executor.tasks;

import com.mobius.software.common.dal.timers.Task;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;

public abstract class MessageTask implements Task {
    protected RtcpBasePacket message;

    public MessageTask(RtcpBasePacket message) {
        this.message = message;
    }

    @Override
    public long getStartTime() {
        return System.currentTimeMillis();
    }

    public String getId() {
        if (this.message.getSSRC() != -1) {
            return String.valueOf(this.message.getSSRC());
        }

        return String.valueOf(System.currentTimeMillis());
    }

    public final RtcpBasePacket getMessage() {
        return this.message;
    }

    public abstract void execute();
}
