package edu.netty.server.channel;

import edu.netty.common.message.Message;
import io.netty.channel.Channel;

public interface ProcessingChannel {
    void process(Message message);
    Channel getChannel();
}
