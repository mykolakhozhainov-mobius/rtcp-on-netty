package edu.netty.server.handler;

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
        Channel channel = ctx.channel();
        MessageChannel messageChannel = messageProcessor.createMessageChannel(channel);

        this.messageProcessor.executor.addTaskLast(new MessageProcessingTask(messageChannel, msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            System.out.println("Read Timeout Received on channel " + ctx.channel() + ", closing channel");
            System.out.println(cause);
            ctx.channel().close();
        } else {
            System.out.println("Exception " + cause.getClass().getName() + " on channel " + ctx.channel() + ", closing channel handle context");
            System.out.println(cause);
            ctx.channel().close();
        }
    }
}
