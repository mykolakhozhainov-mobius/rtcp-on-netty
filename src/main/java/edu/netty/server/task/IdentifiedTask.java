package edu.netty.server.task;

import com.mobius.software.common.dal.timers.Task;

public interface IdentifiedTask extends Task {
    String getId();
}
