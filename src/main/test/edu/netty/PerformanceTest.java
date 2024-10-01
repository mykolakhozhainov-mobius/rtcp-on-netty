package edu.netty;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
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
    public void setUp() throws Exception {
        server = new MessageProcessor(8080);
        server.start();
        server.executor.start(8, 10);

        client = new Client();
        client.start();
        client.executor.start(8, 10);

        messageCounter = new AtomicLong(0L);
        taskCounter = new AtomicLong(0L);
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.bossGroup.shutdownGracefully().sync();
            server.workerGroup.shutdownGracefully().sync();
        }
        if (client != null) {
            client.executor.stop();
        }
    }

    @Test
    public void testMessageProcessingPerformance() throws Exception {
        long startTime = System.currentTimeMillis();

        final int numberOfSessions = 10;
        final int messagesPerSession = 15;
        CountDownLatch latch = new CountDownLatch(numberOfSessions * (messagesPerSession + 1));

        try {
            for (int j = 0; j < numberOfSessions; j++) {
                UUID sessionId = UUID.randomUUID();
                client.createSession(sessionId);
                Session session = client.sessions.get(sessionId);

                if (session == null) {
                    System.err.println("Failed to create session: " + sessionId);
                    continue;
                }

                session.addMessageTask(
                        new Message(session.id, MessageTypeEnum.OPEN, "1"),
                        (callSession, message) -> {
                            callSession.channel.writeAndFlush(message.toByteBuf());
                            messageCounter.incrementAndGet();
                            latch.countDown();
                        }
                );
                taskCounter.incrementAndGet();

                for (int i = 0; i < messagesPerSession; i++) {
                    session.addMessageTask(
                            new Message(session.id, MessageTypeEnum.DATA, "Test message " + i + " for session " + sessionId),
                            (callSession, message) -> {
                                callSession.channel.writeAndFlush(message.toByteBuf());
                                messageCounter.incrementAndGet();
                                latch.countDown();
                            }
                    );
                    taskCounter.incrementAndGet();
                }
            }

            latch.await(30, TimeUnit.SECONDS);

            long duration = System.currentTimeMillis() - startTime;

            assertTrue("Message sending took too long: " + duration + " ms", duration < TimeUnit.SECONDS.toMillis(30));
            assertEquals("Not all messages were sent", latch.getCount(), 0);
        } finally {

            System.out.println("Total tasks added: " + taskCounter.get());
            System.out.println("Total messages sent: " + messageCounter.get());
        }
    }

}