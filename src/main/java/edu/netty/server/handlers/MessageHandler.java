package edu.netty.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;

public abstract class MessageHandler extends ChannelInboundHandlerAdapter {
    public abstract void channelRead(ChannelHandlerContext ctx, Object msg);

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
