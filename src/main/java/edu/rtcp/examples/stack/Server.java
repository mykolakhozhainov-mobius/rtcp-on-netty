package edu.rtcp.examples.stack;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.server.provider.Provider;

import java.net.InetAddress;

public class Server {
    private static final String localLinkID = "1";

    public RtcpStack setupServer(
            int port,
            int remotePort,
            TransportEnum transport,
            int threadPoolSize,
            boolean logging
    ) throws Exception {
        RtcpStack serverStack = new RtcpStack(
                threadPoolSize,
                true,
                transport,
                logging);

        Provider serverProvider = new Provider(serverStack);
        serverStack.registerProvider(serverProvider);

        serverStack.getNetworkManager()
                .addLink(
                        localLinkID,
                        InetAddress.getByName("127.0.0.1"),
                        remotePort,
                        InetAddress.getByName("127.0.0.1"),
                        port);

        serverStack.getNetworkManager().startLink(localLinkID);
        return serverStack;
    }
}
