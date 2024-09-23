package edu.netty.executor;

import edu.netty.task.MessageProcessingTask;

public interface ProcessorExecutor {
    void start(int workersNumber, long taskInterval);
    void stop();
    void addTaskFirst(MessageProcessingTask task);
    void addTaskLast(MessageProcessingTask task);
}
