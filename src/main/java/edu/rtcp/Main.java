package edu.rtcp;

import com.mobius.software.common.dal.timers.WorkerPool;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
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

public class Main {
    private static final String localLinkID = "1";
    private WorkerPool workerPool;

    public RtcpStack setupServer() throws Exception {
        if (workerPool == null) {
            workerPool = new WorkerPool();
            workerPool.start(4);
        }

        RtcpStack serverStack = new RtcpStack(
                4,
                true,
                TransportEnum.UDP
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

                    }
                });
            }

            @Override
            public void onTerminationRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {

            }

            @Override
            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                System.out.println("[SERVER-LISTENER] Received data request");
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
        if (workerPool == null) {
            workerPool = new WorkerPool();
            workerPool.start(4);
        }

        RtcpStack localStack = new RtcpStack(
                4,
                false,
                TransportEnum.UDP);

        Provider localProvider = new Provider(localStack);

        localStack.registerProvider(localProvider);
        localProvider.setClientListener(new ClientSessionListener() {
            @Override
            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                System.out.println("[SERVER-LISTENER] Received data request");
            }

			@Override
			public void onInitialAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
				ClientSession clientSession = (ClientSession) session;

                SenderReport request = localProvider.getPacketFactory().createSenderReport(
                        (byte) 0,
                        response.getSSRC(),
                        null,
                        null
                );
                
                clientSession.sendMessage(request, 8080, new AsyncCallback() {
                    @Override
                    public void onSuccess() {
                    	clientSession.setSessionState(SessionStateEnum.IDLE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
				
			}

			@Override
			public void onTerminationAnswer(RtcpBasePacket response, Session session, AsyncCallback callback) {
				// TODO Auto-generated method stub
				
			}
        });
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
        
        for	(int i = 0; i < 10000; i++ ) {
        	 SenderReport packet =  local.getProvider().getPacketFactory().createSenderReport(
                     (byte) 0,
                     1568+i,
                     null,
                     null
             );

             Session clientSession = local.getProvider()
                     .getSessionFactory()
                     .createClientSession(packet);
             
             Thread.sleep(1);
             clientSession.sendMessage(packet, 8080, new AsyncCallback() {
                 @Override
                 public void onSuccess() {
                	 clientSession.setSessionState(SessionStateEnum.IDLE);
                 }

                 @Override
                 public void onError(Exception e) {

                 }
             });
        }
    }
}
