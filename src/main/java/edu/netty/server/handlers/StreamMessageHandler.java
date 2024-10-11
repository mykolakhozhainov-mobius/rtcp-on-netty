package edu.netty.server.handlers;

import edu.netty.server.channel.AbstractChannel;
import edu.netty.server.processor.MessageProcessor;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;

public class StreamMessageHandler extends AbstractHandler {
    public StreamMessageHandler(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public AbstractChannel createChannel(Channel channel, InetSocketAddress sender) {
        return messageProcessor.createMessageChannel(channel);
    }
}
