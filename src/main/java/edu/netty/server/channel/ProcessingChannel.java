package edu.netty.server.channel;

import edu.netty.common.message.Message;
import io.netty.channel.Channel;

public interface ProcessingChannel {
    boolean isSessioned(Message message);

    void process(Message message);
    Channel getChannel();
}
