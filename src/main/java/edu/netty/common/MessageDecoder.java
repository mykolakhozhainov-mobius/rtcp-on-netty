package edu.netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    private final MessageParser messageParser;

    public MessageDecoder() {
        messageParser = new MessageParser();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        SimpleMessage message = messageParser.parse(in).getMessage();
        out.add(message);
    }
}
