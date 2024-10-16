package edu.rtcp;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.NetworkListener;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.types.ClientSession;
import edu.rtcp.server.session.types.ServerSession;

public class Main {
    public static RtcpStack setupServer() {
        RtcpStack server = new RtcpStack();
        server.setProvider(new Provider(server));

        server.getNetworkManager().addNetworkListener(new NetworkListener() {
            @Override
            public void onMessage(RtcpBasePacket message, Session session, AsyncCallback callback) {
                System.out.println(
                        "[NETWORK] Listener detected new message via session " + session.getId()
                );
            }
        });

        Provider serverProvider = server.getProvider();
        serverProvider.setServerListener(new ServerSessionListener() {
            @Override
            public void onInitialRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                ServerSession serverSession = (ServerSession) session;

                ReceiverReport answer = serverProvider.getPacketFactory().
                        createReceiverReport(
                                (short) 1,
                                false,
                                (short) 0,
                                PacketTypeEnum.RECEIVER_REPORT,
                                0,
                                serverSession.getId(),
                                null
                        );

                serverSession.sendInitialAnswer(answer, new AsyncCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        System.out.println(e);
                    }
                });
            }

            @Override
            public void onTerminationRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                ServerSession serverSession = (ServerSession) session;

                ReceiverReport answer = serverProvider.getPacketFactory().
                        createReceiverReport(
                                (short) 1,
                                false,
                                (short) 0,
                                PacketTypeEnum.RECEIVER_REPORT,
                                0,
                                serverSession.getId(),
                                null
                        );

                serverSession.sendTerminationAnswer(answer, new AsyncCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        System.out.println(e);
                    }
                });
            }

            @Override
            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                System.out.println(request);
            }
        });

        return server;
    }

    public static RtcpStack setupClient() {
        RtcpStack client = new RtcpStack();
        client.setProvider(new Provider(client));

        client.getNetworkManager().addNetworkListener(new NetworkListener() {
            @Override
            public void onMessage(RtcpBasePacket message, Session session, AsyncCallback callback) {
                System.out.println(
                        "[NETWORK] Listener detected new message via session " + session.getId()
                );
            }
        });

        Provider serverProvider = client.getProvider();
        serverProvider.setClientListener(new ClientSessionListener() {
            @Override
            public void onInitialAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {

            }

            @Override
            public void onTerminationAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {

            }

            @Override
            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {

            }
        });

        return client;
    }

    public static void main(String[] args) {
        RtcpStack server = setupServer();
        RtcpStack client = setupClient();

        SenderReport request = client.getProvider()
                .getPacketFactory()
                .createSenderReport(
                        (short) 1,
                        false,
                        (short) 1,
                        PacketTypeEnum.SENDER_REPORT,
                        10,
                        1,
                        0,
                        0,
                        0,
                        0,
                        0,
                        null
                );

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
