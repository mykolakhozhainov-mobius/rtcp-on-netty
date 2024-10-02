package edu.netty.common.message;

import edu.netty.common.DecodingUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Message {
    public UUID sessionId;
    public MessageTypeEnum type;
    public String content;

    public Message() {}

    public Message(String content) {
        this.sessionId = UUID.randomUUID();
        this.type = MessageTypeEnum.DATA;
        this.content = content;
    }

    public Message(MessageTypeEnum type, String content) {
        this.sessionId = UUID.randomUUID();
        this.type = type;
        this.content = content;
    }

    public Message(UUID session, MessageTypeEnum type, String content) {
        this.sessionId = session;
        this.type = type;
        this.content = content;
    }

    @Override
    public String toString() {
        return "\u001B[32m" + "SESSION: " + this.sessionId + "\n" +
                "TYPE: " + this.type + "\n" +
                "CONTENT: " + this.content + "\u001B[0m";
    }

    public ByteBuf toByteBuf() {
        StringBuilder builder = new StringBuilder(this.content);
        builder.setLength(DecodingUtils.CONTENT_SIZE);

        int length = this.content.length();

        for (int i = length; i < DecodingUtils.CONTENT_SIZE; i++) {
            builder.setCharAt(i, ' ');
        }

        return Unpooled.copiedBuffer(
                this.sessionId.toString() + this.type.getID() + builder,
                StandardCharsets.UTF_8
        );
    }
}