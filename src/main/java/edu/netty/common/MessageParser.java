package edu.netty.common;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageParser {
    private Message message;

    private static final byte CR = (byte)'\r';
    private static final byte LF = (byte)'\n';
    private static final char DELIMITER = ':';
    private static final String ENCODING = "UTF-8";

    private static final String SESSION_HEADER = "SESSION";
    private static final String TYPE_HEADER = "TYPE";
    private static final String CONTENT_HEADER = "CONTENT";

    private String getHeaderContent(String header) {
        int dataIndex = header.indexOf(DELIMITER) + 1;

        return header.substring(dataIndex).trim();
    }

    // Session id (UUID) takes 128 bits
    // Message type (ENUM) takes only 2 bits (values: 0, 1, 2, 3)
    // Content takes 128 bits

    private Message parseFields(List<String> lines) {
        Message message = new Message();

        for (String line : lines) {
            if (line.startsWith(SESSION_HEADER)) {
                message.sessionId = UUID.fromString(this.getHeaderContent(line));
            } else if (line.startsWith(TYPE_HEADER)) {
                String content = this.getHeaderContent(line);

                message.type = MessageTypeEnum.from(Integer.parseInt(content));
            } else if (line.startsWith(CONTENT_HEADER)) {
                message.content = this.getHeaderContent(line);
            }
        }

        return message;
    }

    public MessageParser parse(ByteBuf byteBuf) {
        ArrayList<String> lines = new ArrayList<>();

        while (lines.size() < 3) {
            int readerIndex = byteBuf.readerIndex();
            int readableBytes = byteBuf.readableBytes();

            int lfIndex = byteBuf.indexOf(readerIndex, readerIndex + readableBytes, LF);
            int crIndex = -1;

            if (lfIndex >= 1 && byteBuf.getByte(lfIndex - 1) == CR) crIndex = lfIndex - 1;

            int length;
            String line;

            // If \r\n pattern is found or \n
            if ((crIndex != -1 & lfIndex != -1 && lfIndex - crIndex == 1) || (crIndex == -1 && lfIndex != -1)) {
                length = lfIndex - readerIndex;
            } else {
                length = readableBytes;
            }

            line = byteBuf.toString(readerIndex, length, Charset.forName(ENCODING));
            byteBuf.skipBytes(length == readableBytes ? length : length + 1);

            lines.add(line);
        }

        this.message = parseFields(lines);
        return this;
    }

    public Message getMessage() {
        Message message = this.message;
        this.message = null;
        return message;
    }
}
