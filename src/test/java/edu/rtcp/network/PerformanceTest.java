//package edu.rtcp.network;
//
//import edu.rtcp.RtcpStack;
//import edu.rtcp.common.TransportEnum;
//import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
//import edu.rtcp.common.message.rtcp.packet.ApplicationDefined;
//import edu.rtcp.common.message.rtcp.packet.Bye;
//import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
//import edu.rtcp.common.message.rtcp.packet.SenderReport;
//import edu.rtcp.server.callback.AsyncCallback;
//import edu.rtcp.server.provider.Provider;
//import edu.rtcp.server.provider.listeners.ClientSessionListener;
//import edu.rtcp.server.provider.listeners.ServerSessionListener;
//import edu.rtcp.server.session.Session;
//import edu.rtcp.server.session.types.ClientSession;
//import edu.rtcp.server.session.types.ServerSession;
//import edu.rtcp.network.stack.DefaultStackSetup;
//import edu.rtcp.network.stack.StackSetup;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.net.InetSocketAddress;
//import java.util.HashSet;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.junit.Assert.assertEquals;
//
//public class PerformanceTest {
//    // Configurable values ------------------------------------------
//    private static final int TIME_LIMIT = 5000;
//    private static final int THREAD_POOL_SIZE = 4;
//
//    // Message counters ----------------------------------------------
//    private static final AtomicInteger serverReceived = new AtomicInteger(0);
//    private static final AtomicInteger serverSent = new AtomicInteger(0);
//    private static final AtomicInteger clientSent = new AtomicInteger(0);
//    private static final AtomicInteger clientAcks = new AtomicInteger(0);
//
//    // Stacks container -----------------------------------------------
//    private static StackSetup streamStackSetup;
//    private static StackSetup datagramStackSetup;
//
//    @BeforeClass
//    public static void setUpStack() {
//        streamStackSetup = new DefaultStackSetup(TransportEnum.TCP, THREAD_POOL_SIZE);
//        datagramStackSetup = new DefaultStackSetup(TransportEnum.UDP, THREAD_POOL_SIZE);
//    }
//
//    private static final HashSet<Integer> usedIds = new HashSet<>();
//
//    private static int generateId() {
//        int id = Math.abs(new Random().nextInt());
//
//        while (usedIds.contains(id)) {
//            id = Math.abs(new Random().nextInt());
//        }
//
//        usedIds.add(id);
//
//        return id;
//    }
//
//    @After
//    public void clearCounters() {
//        serverReceived.set(0);
//        serverSent.set(0);
//        clientSent.set(0);
//        clientAcks.set(0);
//    }
//
//    public static void setServerListener(StackSetup setup, RtcpStack serverStack) {
//        Provider serverProvider = serverStack.getProvider();
//
//        serverProvider.setServerListener(new ServerSessionListener() {
//            @Override
//            public void onInitialRequest(RtcpBasePacket request, Session session, InetSocketAddress address, AsyncCallback callback) {
//                ServerSession serverSession = (ServerSession) session;
//
//                ReceiverReport answer = serverProvider.getPacketFactory().
//                        createReceiverReport(
//                                (byte) 0,
//                                request.getSSRC(),
//                                null
//                        );
//
//                serverSession.sendInitialAnswer(answer, setup.getClientPort(), new AsyncCallback() {
//                    @Override
//                    public void onSuccess() {
//                        serverSent.incrementAndGet();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//
//                serverReceived.incrementAndGet();
//            }
//
//            @Override
//            public void onTerminationRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
//                ServerSession serverSession = (ServerSession) session;
//
//                ReceiverReport answer = serverProvider.getPacketFactory().
//                        createReceiverReport(
//                                (byte) 0,
//                                request.getSSRC(),
//                                null
//                        );
//
//                serverSession.sendTerminationAnswer(answer, setup.getClientPort(), new AsyncCallback() {
//                    @Override
//                    public void onSuccess() {
//                        serverSent.incrementAndGet();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//
//                serverReceived.incrementAndGet();
//            }
//
//            @Override
//            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
//                ServerSession serverSession = (ServerSession) session;
//
//                ReceiverReport answer = serverProvider.getPacketFactory().
//                        createReceiverReport(
//                                (byte) 0,
//                                request.getSSRC(),
//                                null
//                        );
//
//                serverSession.sendDataAnswer(answer, setup.getClientPort(), new AsyncCallback() {
//                    @Override
//                    public void onSuccess() {
//                        serverSent.incrementAndGet();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//
//                serverReceived.incrementAndGet();
//            }
//        });
//    }
//
//    public static void setClientListener(StackSetup setup, RtcpStack clientStack) {
//        clientStack.getProvider().setClientListener(new ClientSessionListener() {
//            @Override
//            public void onDataAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
//                clientAcks.incrementAndGet();
//
//                Bye byeMessage = clientStack.getProvider().getPacketFactory().createBye(
//                        (byte) 0,
//                        session.getId(),
//                        "Because I wanted so"
//                );
//
//                ClientSession clientSession = (ClientSession) session;
//
//                clientSession.sendTerminationRequest(byeMessage, setup.getServerPort(), new AsyncCallback() {
//                    @Override
//                    public void onSuccess() {
//                        clientSent.incrementAndGet();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//                callback.onSuccess();
//            }
//
//            @Override
//            public void onInitialAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
//                clientAcks.incrementAndGet();
//
//                ApplicationDefined dataPacket = clientStack.getProvider()
//                        .getPacketFactory()
//                        .createApplicationDefined(
//                                (byte) 0,
//                                session.getId(),
//                                "Something",
//                                0
//                        );
//
//                ClientSession clientSession = (ClientSession) session;
//
//                clientSession.sendDataRequest(dataPacket, setup.getServerPort(), new AsyncCallback() {
//                    @Override
//                    public void onSuccess() {
//                        clientSent.incrementAndGet();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//                callback.onSuccess();
//            }
//
//            @Override
//            public void onTerminationAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
//                clientAcks.incrementAndGet();
//                callback.onSuccess();
//            }
//        });
//    }
//
//    public static void startMessageWorkflow(StackSetup setup, RtcpStack client, int sessionNumber) {
//        for (int k = 0; k < sessionNumber; k++) {
//            int sessionId = generateId();
//
//            SenderReport initialPacket = client.getProvider().getPacketFactory().createSenderReport(
//                    (byte) 0,
//                    sessionId,
//                    null,
//                    null
//            );
//
//            ClientSession clientSession = client.getProvider()
//                    .getSessionFactory()
//                    .createClientSession(initialPacket);
//
//            clientSession.sendInitialRequest(initialPacket, setup.getServerPort(), new AsyncCallback() {
//                @Override
//                public void onSuccess() {
//                    clientSent.incrementAndGet();
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }
//    }
//
//    @Test
//    public void testStream() throws Exception {
//        RtcpStack server = streamStackSetup.setupServer();
//        setServerListener(streamStackSetup, server);
//
//        RtcpStack client = streamStackSetup.setupClient();
//        setClientListener(streamStackSetup, client);
//
//        final int numberOfSessions = 10000;
//
//        startMessageWorkflow(streamStackSetup, client, numberOfSessions);
//
//        Thread.sleep(TIME_LIMIT);
//
//        assertEquals(numberOfSessions * 3, serverReceived.get());
//        assertEquals(numberOfSessions * 3, serverSent.get());
//
//        assertEquals(numberOfSessions * 3, clientSent.get());
//        assertEquals(numberOfSessions * 3, clientAcks.get());
//
//        assertEquals(0, streamStackSetup.getServerStack().getProvider().getSessionStorage().size());
//        assertEquals(0, streamStackSetup.getClientStack().getProvider().getSessionStorage().size());
//    }
//
//    @Test
//    public void testDatagram() throws Exception {
//        RtcpStack server = datagramStackSetup.setupServer();
//        setServerListener(datagramStackSetup, server);
//
//        RtcpStack client = datagramStackSetup.setupClient();
//        setClientListener(datagramStackSetup, client);
//
//        final int numberOfSessions = 10000;
//
//        startMessageWorkflow(datagramStackSetup, client, numberOfSessions);
//
//        Thread.sleep(TIME_LIMIT);
//
//        assertEquals(numberOfSessions * 3, serverReceived.get());
//        assertEquals(numberOfSessions * 3, serverSent.get());
//
//        assertEquals(numberOfSessions * 3, clientSent.get());
//        assertEquals(numberOfSessions * 3, clientAcks.get());
//
//        assertEquals(0, datagramStackSetup.getServerStack().getProvider().getSessionStorage().size());
//        assertEquals(0, datagramStackSetup.getClientStack().getProvider().getSessionStorage().size());
//    }
//
//    @AfterClass
//    public static void stopStacks() {
//        streamStackSetup.getServerStack().stop();
//        streamStackSetup.getClientStack().stop();
//
//        datagramStackSetup.getServerStack().stop();
//        datagramStackSetup.getClientStack().stop();
//    }
//}