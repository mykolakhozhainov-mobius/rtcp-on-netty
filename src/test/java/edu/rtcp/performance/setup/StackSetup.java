package edu.rtcp.performance.setup;

import edu.rtcp.RtcpStack;
import edu.rtcp.performance.TestConfig;
import edu.rtcp.server.network.NetworkManager;
import edu.rtcp.server.provider.Provider;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StackSetup {
    public RtcpStack serverStack;
    public RtcpStack clientStack;

    public int serverPort;
    public List<Integer> clientPorts = new ArrayList<>();

    private int generatePort() {
        return new Random().nextInt(10000) + 49152;
    }

    public RtcpStack setupServer() throws Exception {
        this.serverStack = new RtcpStack(
                TestConfig.THREAD_POOL_SIZE,
                true,
                TestConfig.TRANSPORT,
                TestConfig.LOGGING
        );

        serverStack.registerProvider(new Provider(serverStack));

        NetworkManager manager = serverStack.getNetworkManager();
        this.serverPort = 54738;//this.generatePort();

        for (int i = 0; i < TestConfig.CONNECTIONS_NUMBER; i++) {
            int clientPort = this.generatePort();
            this.clientPorts.add(clientPort);

            manager.addLink(
                    String.valueOf(i),
                    InetAddress.getByName(TestConfig.ADDRESS), clientPort,
                    InetAddress.getByName(TestConfig.ADDRESS), this.serverPort
            );
        }

        System.out.println("Server port: " + this.serverPort);

        return serverStack;
    }

    public RtcpStack setupClient() throws Exception {
        this.clientStack = new RtcpStack(
                TestConfig.THREAD_POOL_SIZE,
                false,
                TestConfig.TRANSPORT,
                TestConfig.LOGGING
        );

        this.clientStack.registerProvider(new Provider(clientStack));
        NetworkManager manager = clientStack.getNetworkManager();

        for (int i = 0; i < TestConfig.CONNECTIONS_NUMBER; i++) {
            int clientPort = this.clientPorts.get(i);

            manager.addLink(
                    String.valueOf(i),
                    InetAddress.getByName(TestConfig.ADDRESS), this.serverPort,
                    InetAddress.getByName(TestConfig.ADDRESS), clientPort
            );
        }

        System.out.println("Client ports: " + this.clientPorts);

        return clientStack;
    }

    public void stop() {
        if (this.serverStack != null) {
            this.serverStack.stop();
            this.serverStack = null;
        }

        if (this.clientStack != null) {
            this.clientStack.stop();
            this.clientStack = null;
        }
    }
}
