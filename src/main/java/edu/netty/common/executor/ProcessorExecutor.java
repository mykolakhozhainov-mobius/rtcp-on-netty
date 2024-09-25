package edu.netty.common.executor;

import edu.netty.server.task.MessageProcessingTask;

public interface ProcessorExecutor {
    void start(int workersNumber, long taskInterval);
    void stop();
    void addTaskFirst(MessageProcessingTask task);
    void addTaskLast(MessageProcessingTask task);
}
