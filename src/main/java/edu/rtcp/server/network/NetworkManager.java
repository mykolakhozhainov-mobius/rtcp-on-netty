package edu.rtcp.server.network;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import java.net.InetSocketAddress;

public class NetworkManager {
    private NetworkListener networkListener;
    private final RtcpStack stack;

    public NetworkManager(RtcpStack stack) {
        this.stack = stack;
    }

    public NetworkListener getNetworkListener() {
        if (this.networkListener == null) {
            throw new RuntimeException("Network listener must be defined");
        }

        return this.networkListener;
    }

    public void addNetworkListener(NetworkListener listener) {
        this.networkListener = listener;
    }

    public void sendMessage(Message message, InetSocketAddress address, AsyncCallback callback) {
    }
}
