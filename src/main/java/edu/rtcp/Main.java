package edu.rtcp;

import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.ApplicationDefined;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;
import edu.rtcp.server.session.types.ClientSession;
import edu.rtcp.server.session.types.ServerSession;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final String localLinkID = "1";

    private static final AtomicInteger received = new AtomicInteger(0);
    private static final AtomicInteger sent = new AtomicInteger(0);

    private final HashSet<Integer> usedIds = new HashSet<>();

    private int generateId() {
        int id = Math.abs(new Random().nextInt());

        while (usedIds.contains(id)) {
            id = Math.abs(new Random().nextInt());
        }

        usedIds.add(id);

        return id;
    }

    public RtcpStack setupServer() throws Exception {
        RtcpStack serverStack = new RtcpStack(
                16,
                true,
                TransportEnum.TCP
        );

        Provider serverProvider = new Provider(serverStack);
        serverProvider.setServerListener(new ServerSessionListener() {
            @Override
            public void onInitialRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                ServerSession serverSession = (ServerSession) session;

                ReceiverReport answer = serverProvider.getPacketFactory().
                        createReceiverReport(
                        (byte) 0,
                        request.getSSRC(),
                        null
                );

                serverSession.sendInitialAnswer(answer, 8081, new AsyncCallback() {
                    @Override
                    public void onSuccess() {
                        serverSession.setSessionState(SessionStateEnum.OPEN);
                        System.out.println("[SERVER-LISTENER] Session opened");
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
                                (byte) 0,
                                request.getSSRC(),
                                null
                        );

                serverSession.sendTerminationAnswer(answer, 8081, new AsyncCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

            @Override
            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                received.incrementAndGet();
                System.out.println("[SERVER-LISTENER] Received data request");

                ServerSession serverSession = (ServerSession) session;

                ReceiverReport answer = serverProvider.getPacketFactory().
                        createReceiverReport(
                                (byte) 0,
                                request.getSSRC(),
                                null
                        );

                serverSession.sendDataAnswer(answer, 8081, new AsyncCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });

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

    public RtcpStack setupLocal() throws Exception {
        RtcpStack localStack = new RtcpStack(
                16,
                false,
                TransportEnum.TCP);

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

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        RtcpStack server = main.setupServer();
        RtcpStack local = main.setupLocal();

        local.getProvider().setClientListener(new ClientSessionListener() {
            @Override
            public void onDataAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                System.out.println("[CLIENT-LISTENER] Data ACK from session " + response.getSSRC() + " received");
            }

            @Override
            public void onInitialAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                System.out.println("[CLIENT-LISTENER] Client session state is now OPEN");
            }

            @Override
            public void onTerminationAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                System.out.println("[CLIENT-LISTENER] Client session state is now CLOSED");
            }
        });

        for (int k = 0; k < 1; k++) {
            int sessionId = main.generateId();

            SenderReport packet = local.getProvider().getPacketFactory().createSenderReport(
                    (byte) 0,
                    sessionId,
                    null,
                    null
            );

            ClientSession clientSession = local.getProvider()
                    .getSessionFactory()
                    .createClientSession(packet);

            clientSession.sendInitialRequest(packet, 8080, new AsyncCallback() {
                @Override
                public void onSuccess() {
                    System.out.println("[CLIENT] Initial message sent successfully");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println(e);
                }
            });

            ApplicationDefined dataPacker = local.getProvider()
                    .getPacketFactory()
                    .createApplicationDefined(
                            (byte) 0,
                            sessionId,
                            "Something",
                            0
                    );

//            for (int i = 0; i < 1; i++) {
//                clientSession.sendMessageAndWaitForAck(dataPacker, 8080, new AsyncCallback() {
//                    @Override
//                    public void onSuccess() {
//                        sent.incrementAndGet();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                    }
//                });
//            }

            Bye bye = local.getProvider().getPacketFactory().createBye(
                    (byte) 0,
                    sessionId,
                    "Because I wanted so"
            );

            // TODO: Fix that incomming messages can be processed at the same time

            clientSession.sendTerminationRequest(bye, 8080, new AsyncCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }

        Thread.sleep(5000);

        System.out.println(received.get());
        System.out.println(sent.get());

        System.out.println("Storages:");
        System.out.println("Client: " + local.getProvider().getSessionStorage().size());
        System.out.println("Server: " + server.getProvider().getSessionStorage().size());
    }
}
