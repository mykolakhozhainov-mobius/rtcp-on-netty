package edu.rtcp.server.network;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;

public interface NetworkListener {
    void onMessage(RtcpBasePacket message, Session session, AsyncCallback callback);
}
