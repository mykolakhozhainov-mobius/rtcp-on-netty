package edu.netty.common.decoder;

import edu.netty.common.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class DatagramMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {
    private final MessageParser messageParser;

    public DatagramMessageDecoder() {
        messageParser = new MessageParser();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
        ByteBuf in = msg.content();

        // TODO: UDP size differs from TCP
        if (in.readableBytes() < 13) return;

        Message message = messageParser.parse(in);
        message.setSender(msg.sender());

        out.add(message);
    }
}
