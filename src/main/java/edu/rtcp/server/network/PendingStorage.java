package edu.rtcp.server.network;

import edu.rtcp.server.executor.tasks.MessageTask;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PendingStorage {
    private final Map<Integer, Queue<MessageTask>> pendingTasks = new ConcurrentHashMap<>();

    public void addTask(int sessionId, MessageTask task) {
        Queue<MessageTask> tasksInSession = pendingTasks.get(sessionId);
        if (tasksInSession == null) {
            tasksInSession = new ConcurrentLinkedQueue<>();
        }

        if (tasksInSession.contains(task)) return;

        tasksInSession.add(task);
        this.pendingTasks.put(sessionId, tasksInSession);
    }

    public MessageTask removeTask(int sessionId) {
        Queue<MessageTask> tasksInSession = pendingTasks.get(sessionId);
        if (tasksInSession == null) return null;

        return tasksInSession.poll();
    }

    public boolean isSessionEmpty(int sessionId) {
        Queue<MessageTask> tasksInSession = pendingTasks.get(sessionId);
        return tasksInSession == null || tasksInSession.isEmpty();
    }
}
