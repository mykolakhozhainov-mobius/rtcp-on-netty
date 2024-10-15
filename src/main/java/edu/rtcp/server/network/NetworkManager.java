package edu.rtcp.server.network;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;

public class NetworkManager {
    private NetworkListener networkListener;
    private final RtcpStack stack;

    public NetworkManager(RtcpStack stack) {
        this.stack = stack;
    }

    public void addLink(int port) {
        // TODO: Establish connection with another entity

        // Do not sure that this is obligatory
    }

    public void sendMessage(Message message, int port, AsyncCallback callback) {
        // TODO: Get or create channel with another entity and send message

        // Maybe use stack.getProcessor().channel ?
    }

    public NetworkListener getNetworkListener() {
        return this.networkListener;
    }

    public void setNetworkListener(NetworkListener listener) {
        this.networkListener = listener;
    }
}
