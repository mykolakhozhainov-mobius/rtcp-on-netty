package edu.netty.client;

import edu.netty.common.executor.MessageProcessorExecutor;
import edu.netty.common.message.Message;
import edu.netty.common.session.Session;
import edu.netty.server.task.IdentifiedTask;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractClient {
    protected final String host = "localhost";
    protected int port;

    public Channel channel;
    public MessageProcessorExecutor executor = new MessageProcessorExecutor();
    public Map<UUID, Session> sessions = new HashMap<>();

    protected void createSession(UUID id) {
        if (this.isSessioned(id)) return;

        Session session = new Session(id, channel, executor);
        this.sessions.put(session.id, session);

        System.out.println("[PROCESSOR] UUID " + id + " added as sessioned");
    }

    protected IdentifiedTask makeMessageTask(Message message) {
        return new IdentifiedTask() {
            @Override
            public void execute() {
                channel.writeAndFlush(message.toByteBuf());
            }

            @Override
            public long getStartTime() {
                return System.currentTimeMillis();
            }

            @Override
            public String getId() {
                if (message.sessionId != null) {

                    return message.sessionId.toString();
                }
                return String.valueOf(System.currentTimeMillis());
            }
        };
    }

    protected boolean isSessioned(UUID id) {
        return this.sessions.containsKey(id);
    }

    public abstract void start();
}
