package edu.rtcp.server.network;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;

public interface NetworkListener {
    void onMessage(Message message);
}
