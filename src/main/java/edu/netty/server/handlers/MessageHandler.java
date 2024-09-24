package edu.netty.server.handlers;

import edu.netty.common.SimpleMessage;
import edu.netty.server.MessageProcessor;
import edu.netty.server.channel.MessageChannel;
import edu.netty.server.task.MessageProcessingTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    private final MessageProcessor messageProcessor;

    public MessageHandler(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SimpleMessage message = (SimpleMessage) msg;
        System.out.println("[HANDLER] New message content from " + ctx.channel() + ":");
        System.out.println(message.data);

        Channel channel = ctx.channel();
        MessageChannel messageChannel = messageProcessor.createMessageChannel(channel);

        this.messageProcessor.executor.addTaskLast(new MessageProcessingTask(messageChannel, message));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            System.out.println("[HANDLER] Read Timeout Received on channel " + ctx.channel() + ", closing channel");
        } else {
            System.out.println("[HANDLER] Exception " + cause.getClass().getName() + " on channel " + ctx.channel() + ", closing channel handle context");
        }
        System.out.println(cause);
        ctx.channel().close();
    }
}
