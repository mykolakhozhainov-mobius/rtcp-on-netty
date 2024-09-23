package edu.netty.server.channel;

import io.netty.channel.Channel;

public interface ProcessingChannel {
    void process(Object message);
    Channel getChannel();
}
