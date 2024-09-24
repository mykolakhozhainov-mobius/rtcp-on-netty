package edu.netty.common;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class MessageParser {
    SimpleMessage message;

    public MessageParser parse(ByteBuf byteBuf) {
        StringBuilder content = new StringBuilder();

        int length = byteBuf.readableBytes();

        for (int i = 0; i < length; i++) {
            content.append((char)byteBuf.readByte());
        }

        byteBuf.discardReadBytes();

        message = new SimpleMessage(content.toString());

        return this;
    }

    public SimpleMessage getMessage() {
        SimpleMessage message = this.message;
        this.message = null;
        return message;
    }
}
