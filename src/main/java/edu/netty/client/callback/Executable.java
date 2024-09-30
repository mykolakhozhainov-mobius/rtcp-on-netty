package edu.netty.client.callback;

import edu.netty.common.message.Message;
import edu.netty.common.session.Session;

public interface Executable {
    void execute(Session session, Message message);
}
