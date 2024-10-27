package edu.rtcp.examples;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.ApplicationDefined;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.types.ClientSession;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Random;

public class Client {
    private static final String localLinkID = "1";

    private static final int SESSION_NUMBER = 1000;
    private static final int DATA_MESSAGES = 3;

    private final HashSet<Integer> usedIds = new HashSet<>();

    private int generateId() {
        int id = Math.abs(new Random().nextInt());

        while (usedIds.contains(id)) {
            id = Math.abs(new Random().nextInt());
        }

        usedIds.add(id);

        return id;
    }

    public RtcpStack setupLocal() throws Exception {
        RtcpStack localStack = new RtcpStack(
                32,
                false,
                TransportEnum.TCP,
                true);

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
        Client client = new Client();
        RtcpStack clientStack = client.setupLocal();

        clientStack.getProvider().setClientListener(new ClientSessionListener() {
            @Override
            public void onDataAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                callback.onSuccess();
            }

            @Override
            public void onInitialAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                callback.onSuccess();
            }

            @Override
            public void onTerminationAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
                callback.onSuccess();
            }
        });

        for (int k = 0; k < SESSION_NUMBER; k++) {
            int sessionId = client.generateId();

            SenderReport packet = clientStack.getProvider().getPacketFactory().createSenderReport(
                    (byte) 0,
                    sessionId,
                    null,
                    null
            );

            ClientSession clientSession = clientStack.getProvider()
                    .getSessionFactory()
                    .createClientSession(packet);

            clientSession.sendInitialRequest(packet, 8080, new AsyncCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    System.out.println(e);
                }
            });

            ApplicationDefined dataPacket = clientStack.getProvider()
                    .getPacketFactory()
                    .createApplicationDefined(
                            (byte) 0,
                            sessionId,
                            "Something",
                            0
                    );

            for (int i = 0; i < DATA_MESSAGES; i++) {
                clientSession.sendDataRequest(dataPacket, 8080, new AsyncCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

            Bye bye = clientStack.getProvider().getPacketFactory().createBye(
                    (byte) 0,
                    sessionId,
                    "Because I wanted so"
            );

            clientSession.sendTerminationRequest(bye, 8080, new AsyncCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }

        Thread.sleep(15000);

        System.out.println("Open client sessions: " + clientStack.getProvider().getSessionStorage().size());
    }
}
