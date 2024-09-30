package edu.netty.common;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.UUID;

public class MessageParser {
    private static final Charset CHARSET = CharsetUtil.UTF_8;

    private static final int UUID_SIZE = 36;
    private static final int TYPE_SIZE = 1;
    private static final int CONTENT_SIZE = 2;

    private String readSlice(ByteBuf buf, int length) {
        return buf.readSlice(length).toString(CHARSET);
    }

    public Message parse(ByteBuf byteBuf) {
        UUID sessionId = UUID.fromString(this.readSlice(byteBuf, UUID_SIZE));

        String type = this.readSlice(byteBuf, TYPE_SIZE);
        MessageTypeEnum typeEnum = MessageTypeEnum.from(Integer.parseInt(type));

        String content = this.readSlice(byteBuf, CONTENT_SIZE);

        return new Message(sessionId, typeEnum, content);
    }
}
