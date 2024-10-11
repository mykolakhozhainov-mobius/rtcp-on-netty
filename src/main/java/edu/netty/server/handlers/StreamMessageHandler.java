package edu.netty.server.handlers;

import edu.netty.common.message.Message;
import edu.netty.server.channel.AbstractChannel;
import edu.netty.server.processor.MessageProcessor;
import edu.netty.server.task.MessageProcessingTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class StreamMessageHandler extends AbstractHandler {
    public StreamMessageHandler(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;

        System.out.println("[HANDLER] New message content from " + ctx.channel() + ":");
        System.out.println(message);

        Channel channel = ctx.channel();
        AbstractChannel messageChannel = messageProcessor.createMessageChannel(channel);

        this.messageProcessor.executor.addTaskLast(new MessageProcessingTask(messageChannel, message));
    }
}
