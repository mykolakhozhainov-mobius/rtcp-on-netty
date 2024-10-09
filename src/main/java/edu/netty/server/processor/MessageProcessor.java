package edu.netty.server.processor;

import edu.netty.common.executor.MessageProcessorExecutor;
import edu.netty.server.channel.MessageChannel;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class MessageProcessor {
    public MessageProcessorExecutor executor = new MessageProcessorExecutor();
    public final Set<UUID> sessions = Collections.synchronizedSet(new HashSet<>());

    protected int port;

    public void createSession(UUID id) {
        if (this.isSessioned(id)) return;

        this.sessions.add(id);
        System.out.println("[PROCESSOR] UUID " + id + " added as sessioned");
    }

    public boolean isSessioned(UUID id) {
        return this.sessions.contains(id);
    }

    public abstract MessageChannel createMessageChannel(Channel channel);
    public abstract void start();
}
