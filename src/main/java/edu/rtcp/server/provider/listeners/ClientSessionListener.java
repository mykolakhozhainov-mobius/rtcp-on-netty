package edu.rtcp.server.provider.listeners;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;

public interface ClientSessionListener {
    void onInitialAnswer(RtcpBasePacket response, Session session, AsyncCallback callback);
    void onDataAnswer(RtcpBasePacket response, Session session, AsyncCallback callback);
    void onTerminationAnswer(RtcpBasePacket response, Session session, AsyncCallback callback);
}
