package edu.netty.server.channel;

import edu.netty.common.message.Message;
import edu.netty.server.processor.MessageProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.net.InetAddress;

public abstract class MessageChannel {
    public MessageProcessor messageProcessor;
    protected Channel channel;

    public static String getKey(InetAddress inetAddr, int port) {
        return (inetAddr.getHostAddress() + ":" + port).toLowerCase();
    }

    public Channel getChannel() {
        return this.channel;
    }

    public boolean isSessioned(Message message) {
        return this.messageProcessor.isSessioned(message.sessionId);
    }

    // Abstract methods -------------------------------------

    public abstract void process(Message message);
    public abstract void sendMessage(ByteBuf message);
}
