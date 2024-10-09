package edu.netty.server.handlers;

import edu.netty.common.message.Message;
import edu.netty.server.channel.MessageChannel;
import edu.netty.server.processor.DatagramMessageProcessor;
import edu.netty.server.task.MessageProcessingTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class DatagramMessageHandler extends MessageHandler {
    private final DatagramMessageProcessor datagramMessageProcessor;

    public DatagramMessageHandler(DatagramMessageProcessor datagramMessageProcessor) {
        this.datagramMessageProcessor = datagramMessageProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;
        System.out.println("[HANDLER] New message content from " + ctx.channel() + ":");
        System.out.println(message);

        Channel channel = ctx.channel();
        MessageChannel streamMessageChannel = datagramMessageProcessor.createMessageChannel(channel);

        this.datagramMessageProcessor.executor.addTaskLast(new MessageProcessingTask(streamMessageChannel, message));
    }
}
