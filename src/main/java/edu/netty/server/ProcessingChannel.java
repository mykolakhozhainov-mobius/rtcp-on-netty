package edu.netty.server;

public interface ProcessingChannel {
    void process(Object message);
}
