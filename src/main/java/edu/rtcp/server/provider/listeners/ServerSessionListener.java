package edu.rtcp.server.provider.listeners;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;

public interface ServerSessionListener extends SessionListener {
    void onInitialRequest(RtcpBasePacket request, Session session, AsyncCallback callback);

    void onTerminationRequest(RtcpBasePacket request, Session session, AsyncCallback callback);
}
