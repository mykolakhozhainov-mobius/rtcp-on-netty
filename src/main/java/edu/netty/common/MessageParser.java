package edu.netty.common;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MessageParser {
    private Message message;

    private static final byte CR = (byte)'\r';
    private static final byte LF = (byte)'\n';
    private static final char DELIMITER = ':';
    private static final String ENCODING = "UTF-8";

    private static final String SESSION_HEADER = "SESSION";
    private static final String TYPE_HEADER = "TYPE";
    private static final String MESSAGE_HEADER = "MESSAGE";

    private Message parseFields(List<String> lines) {
        Message message = new Message();
        ArrayList<String> content = new ArrayList<>();
        boolean isContent = false;

        for (String line : lines) {
            int dataIndex = line.indexOf(DELIMITER) + 1;

            if (line.startsWith(SESSION_HEADER)) {
                message.setSession(Integer.parseInt(line.substring(dataIndex).trim()));
            } else if (line.startsWith(TYPE_HEADER)) {
                String type = line.substring(dataIndex).trim();
                message.setMessageType(MessageTypeEnum.valueOf(type));
            } else if (line.startsWith(MESSAGE_HEADER)) {
                String contentLine = line.substring(dataIndex).trim();
                content.add(contentLine);

                isContent = true;
            } else {
                if (isContent) content.add(line);
            }
        }

        message.setData(String.join("\n", content));

        return message;
    }

    public MessageParser parse(ByteBuf byteBuf) {
        ArrayList<String> lines = new ArrayList<>();

        while (byteBuf.readableBytes() > 0) {
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
