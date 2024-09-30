package edu.netty.common.message;

import java.util.HashMap;
import java.util.Map;

public enum MessageTypeEnum {
    DATA(0),
    OPEN(1),
    CLOSE(2),
    ACK(3);

    private final int id;

    MessageTypeEnum(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    private static final Map<Integer, MessageTypeEnum> _map = new HashMap<>();

    static {
        for (MessageTypeEnum difficulty : MessageTypeEnum.values())
            _map.put(difficulty.getID(), difficulty);
    }

    public static MessageTypeEnum from(Integer value) {
        if (value == null) throw new NullPointerException("Enum doesn't contain null values");

        return _map.get(value);
    }
}
