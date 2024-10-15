package edu.rtcp;

import edu.rtcp.server.provider.Provider;

public class Server {
    public static void main(String[] args) {
        RtcpStack server = new RtcpStack(true);
        server.registerProvider(new Provider(server));
    }
}
