package edu.netty.server.handlers;

import edu.netty.common.message.Message;
import edu.netty.server.channel.AbstractChannel;
import edu.netty.server.processor.MessageProcessor;
import edu.netty.server.task.MessageProcessingTask;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;

import java.net.InetSocketAddress;

public abstract class AbstractHandler extends ChannelInboundHandlerAdapter {
    protected MessageProcessor messageProcessor;

    public abstract AbstractChannel createChannel(Channel channel, InetSocketAddress sender);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message message = (Message) msg;

        System.out.println("[HANDLER] New message content from " + ctx.channel() + ":");
        System.out.println(message);

        Channel channel = ctx.channel();
        AbstractChannel messageChannel = this.createChannel(channel, message.sender);

        this.messageProcessor.executor.addTaskLast(new MessageProcessingTask(messageChannel, message));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) {
            System.out.println("[HANDLER] Read Timeout Received on channel " + ctx.channel() + ", closing channel");
        } else {
            System.out.println("[HANDLER] Exception " + cause.getClass().getName() + " on channel " + ctx.channel() + ", closing channel handle context");
        }
        System.out.println(cause);
        ctx.channel().close();
    }
}
