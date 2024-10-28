package edu.rtcp.examples;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.server.provider.Provider;

import java.net.InetAddress;

public class Server {
    private static final String localLinkID = "1";

    public RtcpStack setupServer() throws Exception {
        RtcpStack serverStack = new RtcpStack(
                32,
                true,
                TransportEnum.TCP,
                false
        );

        Provider serverProvider = new Provider(serverStack);
        serverStack.registerProvider(serverProvider);

        serverStack.getNetworkManager()
                .addLink(
                        localLinkID,
                        InetAddress.getByName("127.0.0.1"),
                        8081,
                        InetAddress.getByName("127.0.0.1"),
                        8080);

        serverStack.getNetworkManager().startLink(localLinkID);
        return serverStack;
    }
}
