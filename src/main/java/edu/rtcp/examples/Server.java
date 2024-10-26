package edu.rtcp.examples;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;
import edu.rtcp.server.session.types.ServerSession;

import java.net.InetAddress;

public class Server {
    private static final String localLinkID = "1";

    public RtcpStack setupServer() throws Exception {
        RtcpStack serverStack = new RtcpStack(
                32,
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

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        RtcpStack serverStack = server.setupServer();

        Thread.sleep(15000);

        System.out.println("Open server sessions: " + serverStack.getProvider().getSessionStorage().size());
    }
}
