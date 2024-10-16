//package edu.rtcp;
//
//import edu.rtcp.common.message.Message;
//import edu.rtcp.server.callback.AsyncCallback;
//import edu.rtcp.server.provider.Provider;
//
//public class Client {
//    public static void main(String[] args) throws InterruptedException {
//        RtcpStack client = new RtcpStack(false);
//        client.registerProvider(new Provider(client));
//
//        client.getNetworkManager().addLink(8080);
//        client.getNetworkManager()
//                .sendMessage(
//                        new Message("Hello World!"),
//                        8080,
//                        new AsyncCallback() {
//                            @Override
//                            public void onSuccess() {
//                                System.out.println("Message sent");
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//                                System.out.println("Error: " + e.getMessage());
//                            }
//                        }
//                        );
//    }
//}
