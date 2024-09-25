package edu.netty.common.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class Message {
    private int session = -1;
    private MessageTypeEnum messageType;

    private String data;

    public Message() {}

    public Message(String data) {
        this.data = data;
        this.messageType = MessageTypeEnum.DATA;
    }

    public Message(MessageTypeEnum type, String data) {
        this.data = data;
        this.messageType = type;
    }

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public MessageTypeEnum getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SESSION: " + this.session + "\n" +
                "TYPE: " + this.messageType + "\n" +
                "MESSAGE: " + this.data;
    }

    public ByteBuf toByteBuf() {
        return Unpooled.copiedBuffer(this.toString(), StandardCharsets.UTF_8);
    }
}