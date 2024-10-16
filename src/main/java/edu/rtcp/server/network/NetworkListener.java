package edu.rtcp.server.network;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;

public interface NetworkListener {
    void onMessage(Message message, Session session, AsyncCallback callback);
}
