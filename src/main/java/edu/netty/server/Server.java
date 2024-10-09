package edu.netty.server;

import edu.netty.server.processor.DatagramMessageProcessor;
import edu.netty.server.processor.StreamMessageProcessor;

public class Server {
    public static void main(String[] args) {
        StreamMessageProcessor processor = new StreamMessageProcessor(8080);
        //DatagramMessageProcessor processor = new DatagramMessageProcessor(5060);
        processor.start();

        processor.executor.start(8, 10);
    }
}
