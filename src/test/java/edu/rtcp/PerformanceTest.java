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
import edu.rtcp.server.session.types.ClientSession;
import edu.rtcp.server.session.types.ServerSession;
import edu.rtcp.stack.DefaultStackSetup;
import edu.rtcp.stack.StackSetup;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class PerformanceTest {
    // Configurable values ------------------------------------------
    private static final int SESSION_NUMBER = 10000;
    private static final int TIME_LIMIT = 3000;
    private static final TransportEnum TRANSPORT = TransportEnum.TCP;
    private static final int THREAD_POOL_SIZE = 32;

    // Message counters ----------------------------------------------
    private static final AtomicInteger serverReceived = new AtomicInteger(0);
    private static final AtomicInteger serverSent = new AtomicInteger(0);
    private static final AtomicInteger clientSent = new AtomicInteger(0);
    private static final AtomicInteger clientAcks = new AtomicInteger(0);

    // Stacks container -----------------------------------------------
    private static StackSetup stackSetup;

    @BeforeClass
    public static void setUpStack() throws Exception {
        stackSetup = new DefaultStackSetup(TRANSPORT, THREAD_POOL_SIZE);
    }

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

                serverSession.sendInitialAnswer(answer, stackSetup.getClientPort(), new AsyncCallback() {
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

                serverSession.sendTerminationAnswer(answer, stackSetup.getClientPort(), new AsyncCallback() {
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

                serverSession.sendDataAnswer(answer, stackSetup.getClientPort(), new AsyncCallback() {
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

                clientSession.sendTerminationRequest(byeMessage, stackSetup.getServerPort(), new AsyncCallback() {
                    @Override
                    public void onSuccess() {
                        clientSent.incrementAndGet();
                    }

                    @Override
                    public void onError(Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                callback.onSuccess();
            }

            @Override
            public void onInitialAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                clientAcks.incrementAndGet();

                ApplicationDefined dataPacket = clientStack.getProvider()
                        .getPacketFactory()
                        .createApplicationDefined(
                                (byte) 0,
                                session.getId(),
                                "Something",
                                0
                        );

                ClientSession clientSession = (ClientSession) session;

                clientSession.sendDataRequest(dataPacket, stackSetup.getServerPort(), new AsyncCallback() {
                    @Override
                    public void onSuccess() {
                        clientSent.incrementAndGet();
                    }

                    @Override
                    public void onError(Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                callback.onSuccess();
            }

            @Override
            public void onTerminationAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                clientAcks.incrementAndGet();
                callback.onSuccess();
            }
        });
    }

    @Test
    public void testStreamMessageHandling() throws Exception {
        RtcpStack server = stackSetup.setupServer();
        setServerListener(server);

        RtcpStack client = stackSetup.setupClient();
        setClientListener(client);

        for (int k = 0; k < SESSION_NUMBER; k++) {
            int sessionId = generateId();

            SenderReport initialPacket = client.getProvider().getPacketFactory().createSenderReport(
                    (byte) 0,
                    sessionId,
                    null,
                    null
            );

            ClientSession clientSession = client.getProvider()
                    .getSessionFactory()
                    .createClientSession(initialPacket);

            clientSession.sendInitialRequest(initialPacket, stackSetup.getServerPort(), new AsyncCallback() {
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

        Thread.sleep(TIME_LIMIT);

        assertEquals(SESSION_NUMBER * 3, serverReceived.get());
        assertEquals(SESSION_NUMBER * 3, serverSent.get());

        assertEquals(SESSION_NUMBER * 3, clientSent.get());
        assertEquals(SESSION_NUMBER * 3, clientAcks.get());

        assertEquals(0, stackSetup.getServerStack().getProvider().getSessionStorage().size());
        assertEquals(0, stackSetup.getClientStack().getProvider().getSessionStorage().size());
    }

    @After
    public void stopStacks() {
        stackSetup.getServerStack().stop();
        stackSetup.getClientStack().stop();
    }
}