package edu.rtcp.examples.stack;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.server.provider.Provider;

import java.net.InetAddress;

public class Client {
    private static final String localLinkID = "1";

    public RtcpStack setupLocal(
            int port,
            int remotePort,
            TransportEnum transport,
            int threadPoolSize,
            boolean logging
    ) throws Exception {
        RtcpStack localStack = new RtcpStack(
                threadPoolSize,
                false,
                transport,
                logging);

        Provider localProvider = new Provider(localStack);

        localStack.registerProvider(localProvider);
        localStack.getNetworkManager()
                .addLink(
                        localLinkID,
                        InetAddress.getByName("127.0.0.1"),
                        remotePort,
                        InetAddress.getByName("127.0.0.1"),
                        port
                );

        localStack.getNetworkManager().startLink(localLinkID);
        return localStack;
    }
}
