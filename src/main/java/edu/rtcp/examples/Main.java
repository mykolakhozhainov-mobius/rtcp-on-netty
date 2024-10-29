package edu.rtcp.examples;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.factory.PacketFactory;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.common.message.rtcp.parts.ReportBlock;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.types.ClientSession;
import edu.rtcp.server.session.types.ServerSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final AtomicInteger serverReceived = new AtomicInteger(0);
    private static final AtomicInteger serverSent = new AtomicInteger(0);

    private static final AtomicInteger clientSent = new AtomicInteger(0);
    private static final AtomicInteger clientAcks = new AtomicInteger(0);

    private static final AsyncCallback SENT_CALLBACK = new AsyncCallback() {
        @Override
        public void onSuccess() {
            clientSent.incrementAndGet();
        }

        @Override
        public void onError(Exception e) {
            throw new RuntimeException(e);
        }
    };

    private static final int SESSION_NUMBER = 10000;
    private static final TransportEnum TRANSPORT = TransportEnum.UDP;
    private static final boolean LOGGING = true;
    private static final int THREAD_POOL_SIZE = 4;

    private static final HashSet<Integer> usedIds = new HashSet<>();

    private static int generateId() {
        int id = Math.abs(new Random().nextInt());

        while (usedIds.contains(id)) {
            id = Math.abs(new Random().nextInt());
        }

        usedIds.add(id);

        return id;
    }

    public static void setServerListener(RtcpStack serverStack) {
        Provider serverProvider = serverStack.getProvider();

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
                        serverSent.incrementAndGet();
                    }

                    @Override
                    public void onError(Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                serverReceived.incrementAndGet();
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
                        serverSent.incrementAndGet();
                    }

                    @Override
                    public void onError(Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                serverReceived.incrementAndGet();
            }

            @Override
            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
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
                        serverSent.incrementAndGet();
                    }

                    @Override
                    public void onError(Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                serverReceived.incrementAndGet();
            }
        });
    }

    public static void setClientListener(RtcpStack clientStack) {
        clientStack.getProvider().setClientListener(new ClientSessionListener() {
            @Override
            public void onDataAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                clientAcks.incrementAndGet();

                Bye byeMessage = clientStack.getProvider().getPacketFactory().createBye(
                        (byte) 0,
                        session.getId(),
                        "Because I wanted so"
                );

                ClientSession clientSession = (ClientSession) session;

                clientSession.sendTerminationRequest(byeMessage, 8080, SENT_CALLBACK);
                callback.onSuccess();
            }

            @Override
            public void onInitialAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                clientAcks.incrementAndGet();

                PacketFactory factory = clientStack.getProvider().getPacketFactory();

                SenderReport dataPacket = createCustomSenderReport(factory, 7, session.getId(), 7);

                ClientSession clientSession = (ClientSession) session;

                clientSession.sendDataRequest(dataPacket, 8080, SENT_CALLBACK);
                callback.onSuccess();
            }

            @Override
            public void onTerminationAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                clientAcks.incrementAndGet();
                callback.onSuccess();
            }
        });
    }

    private static SenderReport createCustomSenderReport(PacketFactory factory, int itemCount, int ssrc, int numberOfBlocks) {
        List<ReportBlock> blocks = new ArrayList<>();

        for (int i = 0; i < numberOfBlocks; i++) {
            blocks.add(factory.createReportBlock(ssrc, (byte) i));
        }

        return factory.createSenderReport(
                (byte) itemCount,
                ssrc,
                blocks,
                null
        );
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        RtcpStack serverStack = server.setupServer(
                8080,
                8081,
                TRANSPORT,
                THREAD_POOL_SIZE,
                LOGGING
        );
        setServerListener(serverStack);

        Client client = new Client();
        RtcpStack clientStack = client.setupLocal(
                8081,
                8080,
                TRANSPORT,
                THREAD_POOL_SIZE,
                LOGGING
        );
        setClientListener(clientStack);

        for (int k = 0; k < SESSION_NUMBER; k++) {
            int sessionId = generateId();

            PacketFactory factory = clientStack.getProvider().getPacketFactory();

            SenderReport initialPacket = createCustomSenderReport(factory, 0, sessionId, 5);

            ClientSession clientSession = clientStack.getProvider()
                    .getSessionFactory()
                    .createClientSession(initialPacket);

            clientSession.sendInitialRequest(initialPacket, 8080, new AsyncCallback() {
                @Override
                public void onSuccess() {
                    clientSent.incrementAndGet();
                }

                @Override
                public void onError(Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        Thread.sleep(5000);

        serverStack.stop();
        clientStack.stop();

        System.out.println("===== SERVER STATS =====");
        System.out.println("RECEIVED: " + serverReceived.get());
        System.out.println("SENT: " + serverSent.get());
        System.out.println("OPEN SESSIONS: " + serverStack.getProvider().getSessionStorage().size());

        System.out.println("===== CLIENT STATS =====");
        System.out.println("SENT: " + clientSent.get());
        System.out.println("ACKS: " + clientAcks.get());

        System.out.println("OPEN SESSIONS: " + clientStack.getProvider().getSessionStorage().size());
        System.exit(0);
    }
}
