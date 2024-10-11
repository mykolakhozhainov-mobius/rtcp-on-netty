package edu.netty.common.decoder;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import java.util.UUID;

public class MessageParser {
    private String readSlice(ByteBuf buf, int length) {
        return buf.readSlice(length).toString(DecodingUtils.CHARSET);
    }

    public Message parse(ByteBuf byteBuf) {
        UUID sessionId = UUID.fromString(this.readSlice(byteBuf, DecodingUtils.UUID_SIZE));

        String type = this.readSlice(byteBuf, DecodingUtils.TYPE_SIZE);
        MessageTypeEnum typeEnum = MessageTypeEnum.from(Integer.parseInt(type));

        String content = this.readSlice(byteBuf, DecodingUtils.CONTENT_SIZE).trim();

        return new Message(sessionId, typeEnum, content);
    }
}
