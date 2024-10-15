//package edu.rtcp;
//
//import edu.rtcp.common.message.Message;
//import edu.rtcp.server.callback.AsyncCallback;
//import edu.rtcp.server.network.NetworkListener;
//import edu.rtcp.server.provider.Provider;
//import edu.rtcp.server.provider.listeners.ClientSessionListener;
//import edu.rtcp.server.provider.listeners.ServerSessionListener;
//import edu.rtcp.server.session.types.ClientSession;
//import edu.rtcp.server.session.Session;
//import org.junit.Test;
//
//import java.net.InetSocketAddress;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class StackTest extends StackTestBase {
//    @Test
//    public void testStack() {
//        super.setupServer();
//        super.setupLocal();
//
//        final AtomicInteger sentMessages = new AtomicInteger(0);
//
//        // TODO: Add Network Manager
//        this.localStack.getNetworkManager().setNetworkListener(new NetworkListener() {
//            @Override
//            public void onMessage(Message message) {
//                sentMessages.incrementAndGet();
//            }
//        });
//
//        final AtomicInteger receivedMessages = new AtomicInteger(0);
//
//        this.localStack.getNetworkManager().setNetworkListener(new NetworkListener() {
//            @Override
//            public void onMessage(Message message) {
//                receivedMessages.incrementAndGet();
//            }
//        });
//
//        Provider serverProvider = this.serverStack.getProvider();
//        serverProvider.setListener(new ServerSessionListener() {
//            @Override
//            public void onInitialRequest(Message request, Session session, AsyncCallback callback) {
//                Message answer = new Message();
//
////                session.sendAnswer(answer, session.remoteAddress, new AsyncCallback() {
////                    @Override
////                    public void onSuccess() {}
////
////                    @Override
////                    public void onError(Exception e) {
////                        System.out.println(e);
////                    }
////                });
//            }
//
//            @Override
//            public void onTerminationRequest(Message request, Session session, AsyncCallback callback) {
//
//            }
//
//            @Override
//            public void onDataRequest(Message request, Session session, AsyncCallback callback) {
//                System.out.println(request);
//            }
//        });
//
//        Provider clientProvider = this.localStack.getProvider();
//        clientProvider.setListener(new ClientSessionListener() {
//            @Override
//            public void onInitialResponse(Message response, Session session, AsyncCallback callback) {
//                System.out.println("Response for session initialization got");
//            }
//
//            @Override
//            public void onDataRequest(Message request, Session session, AsyncCallback callback) {
//                System.out.println(request);
//            }
//        });
//
//        // TODO: Send initial request
//        Message request = clientProvider.getMessageFactory().createRequest();
//        ClientSession clientSession = (ClientSession) clientProvider.getSessionFactory().createClientSession(request);
//
//        InetSocketAddress address = new InetSocketAddress(SERVER_PORT);
//        clientSession.sendInitialRequest(request, address, new AsyncCallback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
//    }
//}
