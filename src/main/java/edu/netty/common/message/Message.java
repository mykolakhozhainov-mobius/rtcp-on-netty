package edu.netty.common.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class Message {
    private int session = -1;
    private MessageTypeEnum messageType;

    public String data;

    public Message(String data) {
        this.data = data;
        this.messageType = MessageTypeEnum.DATA;
    }

    public Message(MessageTypeEnum type, String data) {
        this.data = data;
        this.messageType = type;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public ByteBuf toByteBuf() {
        String message = "SESSION: " + this.session + "\n" +
                "TYPE: " + this.messageType + "\n" +
                "MESSAGE: " + this.data + "\n";

        return Unpooled.copiedBuffer(message, StandardCharsets.UTF_8);
    }
}