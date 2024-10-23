package edu.rtcp.server.provider.listeners;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;

public interface SessionListener {
    void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback);
}
