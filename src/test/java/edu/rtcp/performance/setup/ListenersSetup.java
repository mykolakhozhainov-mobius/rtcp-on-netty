package edu.rtcp.performance.setup;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
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

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class ListenersSetup {
    public static final AtomicInteger serverReceived = new AtomicInteger(0);
    public static final AtomicInteger serverSent = new AtomicInteger(0);

    public static final AtomicInteger clientSent = new AtomicInteger(0);
    public static final AtomicInteger clientAcks = new AtomicInteger(0);

    public static void setServerListener(RtcpStack serverStack) {
        Provider serverProvider = serverStack.getProvider();

        serverProvider.setServerListener(new ServerSessionListener() {
            @Override
            public void onInitialRequest(RtcpBasePacket request, Session session, InetSocketAddress address, AsyncCallback callback) {
                ServerSession serverSession = (ServerSession) session;

                ReceiverReport answer = PacketUtils.createResponse(request.getSSRC());

                serverSession.sendInitialAnswer(answer, address, new AsyncCallback() {
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
            public void onTerminationRequest(RtcpBasePacket request, Session session, InetSocketAddress address, AsyncCallback callback) {
                ServerSession serverSession = (ServerSession) session;

                ReceiverReport answer = PacketUtils.createResponse(request.getSSRC());

                serverSession.sendTerminationAnswer(answer, address, new AsyncCallback() {
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
            public void onDataRequest(RtcpBasePacket request, Session session, InetSocketAddress address, AsyncCallback callback) {
                ServerSession serverSession = (ServerSession) session;

                ReceiverReport answer = PacketUtils.createResponse(request.getSSRC());

                serverSession.sendDataAnswer(answer, address, new AsyncCallback() {
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

                Bye bye = PacketUtils.createBye(response.getSSRC());

                ClientSession clientSession = (ClientSession) session;

                clientSession.sendTerminationRequest(bye, null, new AsyncCallback() {
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

                SenderReport dataPacket = PacketUtils.createData(response.getSSRC());

                ClientSession clientSession = (ClientSession) session;

                clientSession.sendDataRequest(dataPacket, null, new AsyncCallback() {
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
}
