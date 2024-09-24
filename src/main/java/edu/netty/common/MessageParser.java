package edu.netty.common;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class MessageParser {
    private SimpleMessage message;

    private static final byte CR = (byte)'\r';
    private static final byte LF = (byte)'\n';
    private static final String ENCODING = "UTF-8";

    public MessageParser parse(ByteBuf byteBuf) {
        int readerIndex = byteBuf.readerIndex();
        int readableBytes = byteBuf.readableBytes();

        int lfIndex = byteBuf.indexOf(readerIndex, readerIndex + readableBytes, LF);
        int crIndex = -1;

        if (lfIndex >= 1 && byteBuf.getByte(lfIndex-1) == CR) crIndex = lfIndex - 1;

        String line;
        if (crIndex != -1 & lfIndex != -1 && lfIndex - crIndex == 1) {
            int length = lfIndex - readerIndex + 1;
            line = byteBuf.toString(readerIndex, length, Charset.forName(ENCODING));
            byteBuf.skipBytes(length);
        } else {
            line = byteBuf.toString(readerIndex, readableBytes, Charset.forName(ENCODING));
            byteBuf.skipBytes(readableBytes);
        }
        this.message = new SimpleMessage(line);

        return this;
    }

    public SimpleMessage getMessage() {
        SimpleMessage message = this.message;
        this.message = null;
        return message;
    }
}
