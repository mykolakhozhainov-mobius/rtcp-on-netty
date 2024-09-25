package edu.netty.common.executor;

import edu.netty.server.task.IdentifiedTask;

public interface ProcessorExecutor {
    void start(int workersNumber, long taskInterval);
    void stop();
    void addTaskFirst(IdentifiedTask task);
    void addTaskLast(IdentifiedTask task);
}
