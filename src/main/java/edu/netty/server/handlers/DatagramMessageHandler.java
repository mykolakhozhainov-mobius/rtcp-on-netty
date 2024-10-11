package edu.netty.server.handlers;

import edu.netty.server.channel.AbstractChannel;
import edu.netty.server.processor.MessageProcessor;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class DatagramMessageHandler extends AbstractHandler {
    public DatagramMessageHandler(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public AbstractChannel createChannel(Channel channel, InetSocketAddress sender) {
        return messageProcessor.createMessageChannel(channel, sender);
    }
}
