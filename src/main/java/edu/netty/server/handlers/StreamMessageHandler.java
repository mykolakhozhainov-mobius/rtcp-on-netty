package edu.netty.server.handlers;

import edu.netty.common.message.Message;
import edu.netty.server.channel.transports.StreamMessageChannel;
import edu.netty.server.processor.StreamMessageProcessor;
import edu.netty.server.task.MessageProcessingTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class StreamMessageHandler extends MessageHandler {
    private final StreamMessageProcessor streamMessageProcessor;

    public StreamMessageHandler(StreamMessageProcessor streamMessageProcessor) {
        this.streamMessageProcessor = streamMessageProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;
        System.out.println("[HANDLER] New message content from " + ctx.channel() + ":");
        System.out.println(message);

        Channel channel = ctx.channel();
        StreamMessageChannel streamMessageChannel = (StreamMessageChannel) streamMessageProcessor.createMessageChannel(channel);

        this.streamMessageProcessor.executor.addTaskLast(new MessageProcessingTask(streamMessageChannel, message));
    }
}
