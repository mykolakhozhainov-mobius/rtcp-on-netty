package edu.netty.common;

import edu.netty.common.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    public static final int MAX_FRAME_SIZE = 39;

    private final MessageParser messageParser;

    public MessageDecoder() {
        messageParser = new MessageParser();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < MAX_FRAME_SIZE) return;

        Message message = messageParser.parse(in);
        out.add(message);
    }
}
