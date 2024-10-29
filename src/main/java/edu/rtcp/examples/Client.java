package edu.rtcp.examples;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.server.provider.Provider;

import java.net.InetAddress;

public class Client {
    private static final String localLinkID = "1";
    private static final int THREAD_POOL_SIZE = 4;

    public RtcpStack setupLocal(TransportEnum transport, boolean logging) throws Exception {
        RtcpStack localStack = new RtcpStack(
                THREAD_POOL_SIZE,
                false,
                transport,
                logging);

        Provider localProvider = new Provider(localStack);

        localStack.registerProvider(localProvider);
        localStack.getNetworkManager()
                .addLink(
                        localLinkID,
                        InetAddress.getByName("127.0.0.1"),
                        8080,
                        InetAddress.getByName("127.0.0.1"),
                        8081
                );

        localStack.getNetworkManager().startLink(localLinkID);
        return localStack;
    }
}
