package edu.rtcp.server.provider.listeners;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;

public interface ServerSessionListener extends SessionListener {
    void onInitialRequest(Message request, Session session, AsyncCallback callback);

    void onTerminationRequest(Message request, Session session, AsyncCallback callback);
}
