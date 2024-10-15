package edu.rtcp;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.processor.transport.StreamProcessor;
import edu.rtcp.server.provider.Provider;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RtcpStack server = new RtcpStack();
        Provider provider = new Provider(server);

        StreamProcessor processor = new StreamProcessor(8080, server);
        server.setProvider(provider);
        server.setProcessor(processor);

        processor.run(4, 1000);

        RtcpStack client = new RtcpStack();
        Provider clientProvider = new Provider(client);

        StreamProcessor clientProcessor = new StreamProcessor(8081, client);
        client.setProvider(clientProvider);
        client.setProcessor(clientProcessor);

        clientProcessor.run(4, 1000);

        client.getNetworkManager().sendMessage(new Message("HELLO"), new InetSocketAddress(8080), new AsyncCallback() {
            @Override
            public void onSuccess() {
                System.out.println("Message sent");
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Error sending message");
            }
        });
    }
}
