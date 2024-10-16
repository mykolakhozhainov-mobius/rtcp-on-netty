package edu.rtcp;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.NetworkListener;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.types.ClientSession;

public class Main {
    public static RtcpStack setupServer() {
        RtcpStack server = new RtcpStack();
        server.setProvider(new Provider(server));

        server.getNetworkManager().addNetworkListener(new NetworkListener() {
            @Override
            public void onMessage(Message message, Session session, AsyncCallback callback) {
                // Here we can check any message
            }
        });

        Provider serverProvider = server.getProvider();
        serverProvider.setServerListener(new ServerSessionListener() {
            @Override
            public void onInitialRequest(Message request, Session session, AsyncCallback callback) {
                // TODO: Set some session creation logic
            }

            @Override
            public void onTerminationRequest(Message request, Session session, AsyncCallback callback) {
                // TODO: Set some session termination logic
            }

            @Override
            public void onDataRequest(Message request, Session session, AsyncCallback callback) {
                // TODO: Set some data message handling logic
            }
        });

        return server;
    }

    public static RtcpStack setupClient() {
        RtcpStack client = new RtcpStack();
        client.setProvider(new Provider(client));

        client.getNetworkManager().addNetworkListener(new NetworkListener() {
            @Override
            public void onMessage(Message message, Session session, AsyncCallback callback) {
                // Here we can check any message
            }
        });

        Provider serverProvider = client.getProvider();
        serverProvider.setClientListener(new ClientSessionListener() {
            @Override
            public void onInitialAnswer(Message response, Session session, AsyncCallback callback) {
                // TODO: Set some session creation ACK logic
            }

            @Override
            public void onTerminationAnswer(Message response, Session session, AsyncCallback callback) {
                // TODO: Set some session deletion ACK logic
            }

            @Override
            public void onDataRequest(Message request, Session session, AsyncCallback callback) {
                // TODO: Set some data message ACK logic
            }
        });

        return client;
    }

    public static void main(String[] args) {
        RtcpStack server = setupServer();
        RtcpStack client = setupClient();

        // TODO: replace raw message creation with message factory
        Message request = new Message();
        ClientSession clientSession = client.getProvider()
                .getSessionFactory()
                .createClientSession(request);

        clientSession.sendInitialRequest(request, new AsyncCallback() {
            @Override
            public void onSuccess() {
                System.out.println("Session creation message is sent");
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Session creation failed");
            }
        });

        // TODO: Check if client's session is IDLE
    }
}
