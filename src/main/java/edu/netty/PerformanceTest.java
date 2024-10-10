package edu.netty;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;

import edu.netty.client.Client;
import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import edu.netty.common.session.Session;
import edu.netty.server.MessageProcessor;

public class PerformanceTest {

    private MessageProcessor server;
    private Client client;
    private AtomicLong messageCounter;
    private AtomicLong taskCounter;

    @Before
    public void setUp() throws Exception
    {
        server = new MessageProcessor(8080);
        server.start();
        server.executor.start(16, 10);

        client = new Client();
        client.start();
        client.executor.start(16, 10);

       
        Thread.sleep(1000); 

        messageCounter = new AtomicLong(0L);
        taskCounter = new AtomicLong(0L);
    }

    @Test
    public void testMessageProcessingPerformance() throws Exception 
    {
        long startTime = System.currentTimeMillis();

        final int numberOfSessions = 1000;
        final int messagesPerSession = 3;
        final long expectedMessages = numberOfSessions * (messagesPerSession + 1);

        for (int j = 0; j < numberOfSessions; j++) 
        {
            UUID sessionId = UUID.randomUUID();
            client.createSession(sessionId);
            Session session = client.sessions.get(sessionId);

           
            if (session == null || !session.channel.isActive()) 
            {
                System.err.println("Failed to create session or channel is not active: " + sessionId);
                continue;
            }

            session.addMessageTask(
                    new Message(session.id, MessageTypeEnum.OPEN, "OPEN message type for the session" + sessionId),
                    (callSession, message) -> {
                        callSession.channel.writeAndFlush(message.toByteBuf()).addListener(future -> {
                            if (future.isSuccess()) {
                                messageCounter.incrementAndGet();
                            }
                        });
                    }
            );
            taskCounter.incrementAndGet();

            for (int i = 0; i < messagesPerSession; i++) {
                session.addMessageTask(
                        new Message(session.id, MessageTypeEnum.DATA, "Test message " + i + " for session " + sessionId),
                        (callSession, message) -> {
                            callSession.channel.writeAndFlush(message.toByteBuf()).addListener(future -> {
                                if (future.isSuccess()) {
                                    messageCounter.incrementAndGet();
                                }
                            });
                        }
                );
                taskCounter.incrementAndGet();
            }
        }

        
        while (messageCounter.get() < expectedMessages) {
            Thread.sleep(500); 
        }

        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Test duration: " + duration + " ms");
        System.out.println("Total tasks added: " + taskCounter.get());
        System.out.println("Total messages acknowledged: " + messageCounter.get());

        
        assertEquals("Not all messages were acknowledged", taskCounter.get(), messageCounter.get());
    }
}
