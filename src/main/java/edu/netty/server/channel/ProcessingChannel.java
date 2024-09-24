package edu.netty.server.channel;

import edu.netty.common.SimpleMessage;
import io.netty.channel.Channel;

public interface ProcessingChannel {
    void process(SimpleMessage message);
    Channel getChannel();
}
