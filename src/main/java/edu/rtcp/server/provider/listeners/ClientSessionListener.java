package edu.rtcp.server.provider.listeners;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;

public interface ClientSessionListener extends SessionListener {
    void onInitialResponse(Message response, Session session, AsyncCallback callback);
}
