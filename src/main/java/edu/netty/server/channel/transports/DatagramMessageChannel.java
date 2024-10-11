package edu.netty.server.channel.transports;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import edu.netty.server.channel.AbstractChannel;
import edu.netty.server.processor.MessageProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;

public class DatagramMessageChannel extends AbstractChannel {
    private InetAddress remoteAddress;
    private int remotePort;

    public DatagramMessageChannel(MessageProcessor messageProcessor, Channel channel, InetSocketAddress sender) {
        this.messageProcessor = messageProcessor;
        this.channel = channel;

        if (sender != null) {
            this.remoteAddress = sender.getAddress();
            this.remotePort = sender.getPort();
        }
    }

    public String getKey() {
        return DatagramMessageChannel.getKey(remoteAddress, remotePort);
    }

    @Override
    public void process(Message message) {
        UUID sessionId = message.sessionId;
        MessageTypeEnum type = message.type;

        if (type == MessageTypeEnum.OPEN) {
            this.messageProcessor.createSession(sessionId);
        }

        if (this.messageProcessor.isSessioned(sessionId)) {
            this.sendMessage(new Message(
                    sessionId,
                    MessageTypeEnum.ACK,
                    message.content
            ).toByteBuf());
        }

        System.out.println("[CHANNEL] Channel " + channel.id() + " proceeded message");
    }

    @Override
    public void sendMessage(ByteBuf message) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(message);

        channel.writeAndFlush(new DatagramPacket(byteBuf, new InetSocketAddress(remoteAddress, remotePort)));
    }
}
