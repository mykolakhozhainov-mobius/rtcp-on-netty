package edu.netty.server.channel;

public interface ProcessingChannel {
    void process(Object message);
}
