package edu.netty.common.message;

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
        return "SESSION: " + this.sessionId + "\n" +
                "TYPE: " + this.type + "\n" +
                "CONTENT: " + this.content + "\n";
    }

    public ByteBuf toByteBuf() {
        return Unpooled.copiedBuffer(this.toString(), StandardCharsets.UTF_8);
    }
}