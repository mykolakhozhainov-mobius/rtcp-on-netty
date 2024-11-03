package edu.rtcp.performance;

import com.mobius.software.common.dal.timers.Timer;
import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.performance.setup.ListenersSetup;
import edu.rtcp.performance.setup.PacketUtils;
import edu.rtcp.performance.setup.StackSetup;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.types.ClientSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PerformanceTest {
    private static final StackSetup stackSetup = new StackSetup();
    private static final HashSet<Integer> usedIds = new HashSet<>();

    private static final Logger logger= LogManager.getLogger(PerformanceTest.class);

    private static int getSessionId() {
        int id = Math.abs(new Random().nextInt());

        while (usedIds.contains(id)) {
            id = Math.abs(new Random().nextInt());
        }

        usedIds.add(id);

        return id;
    }

    @Test
    public void testStreamWorkflow() throws Exception {
        // Preconditions: setting up stacks
        RtcpStack serverStack = stackSetup.setupServer();
        RtcpStack clientStack = stackSetup.setupClient();

        ListenersSetup.setServerListener(serverStack);
        ListenersSetup.setClientListener(clientStack);

        // Preconditions: start links
        serverStack.getNetworkManager().startAllLinks();
        Thread.sleep(TestConfig.IDLE_TIMEOUT);
        clientStack.getNetworkManager().startAllLinks();

        // Test Action: sending messages (size > 200)
        long startTime = System.currentTimeMillis() + 1000;
        int chunk = TestConfig.SESSION_NUMBER / (TestConfig.INIT_TIME * 10);

        logger.info("Chunk size: {}", chunk);

        for (int k = 0; k < TestConfig.SESSION_NUMBER; k++) {
            int sessionId = getSessionId();
            if ((k % chunk) == (chunk - 1)) startTime += 100;

            SenderReport initialPacket = PacketUtils.createInitial(sessionId);

            ClientSession clientSession = clientStack.getProvider()
                    .getSessionFactory()
                    .createClientSession(initialPacket);

            final long currTime = startTime;
            clientStack.getMessageExecutor().getPeriodicQueue().store(startTime, new Timer() {
                @Override
                public Long getRealTimestamp() {
                    return currTime;
                }

                @Override
                public void stop() {

                }

                @Override
                public void execute() {
                    clientSession.sendInitialRequest(initialPacket, null, new AsyncCallback() {
                        @Override
                        public void onSuccess() {
                            ListenersSetup.clientInitialRequests.incrementAndGet();
                        }

                        @Override
                        public void onError(Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                @Override
                public long getStartTime() {
                    return currTime;
                }
            });
        }

        // Test Action: stopping and removing stacks
        Thread.sleep(TestConfig.RESPONSE_TIMEOUT);
        stackSetup.stop();

        // Output the stats
        System.out.println("===== SERVER STATS =====");
        System.out.println("INITIAL: " + ListenersSetup.serverInitialRequests.get());
        System.out.println("DATA: " + ListenersSetup.serverDataRequests.get());
        System.out.println("TERMINATION: " + ListenersSetup.serverTerminationRequests.get());
        System.out.println("ACKS SENT: " + ListenersSetup.serverAckSent.get());
        System.out.println("SESSIONS LEFT: " + serverStack.getProvider().getSessionStorage().size());

        System.out.println("===== CLIENT STATS =====");
        System.out.println("INITIAL: " + ListenersSetup.clientInitialRequests.get());
        System.out.println("DATA: " + ListenersSetup.clientDataRequests.get());
        System.out.println("TERMINATION: " + ListenersSetup.clientTerminationRequests.get());
        System.out.println("ACKS RECEIVED: " + ListenersSetup.clientAckReceived.get());
        System.out.println("SESSIONS LEFT: " + clientStack.getProvider().getSessionStorage().size());

        // Test Case 1: asserting numbers of messages
        final int expectedMessages = TestConfig.SESSION_NUMBER * 3;

        int clientSent = ListenersSetup.clientInitialRequests.get() + ListenersSetup.clientDataRequests.get() + ListenersSetup.clientTerminationRequests.get();
        int serverReceived = ListenersSetup.serverInitialRequests.get() + ListenersSetup.serverDataRequests.get() + ListenersSetup.serverTerminationRequests.get();

        assertEquals(expectedMessages, serverReceived);
        assertEquals(expectedMessages, ListenersSetup.serverAckSent.get());
        assertEquals(expectedMessages, clientSent);
        assertEquals(expectedMessages, ListenersSetup.clientAckReceived.get());

        // Test Case 2: asserting numbers of open sessions
        final int expectedSessions = 0;

        assertEquals(expectedSessions, serverStack.getProvider().getSessionStorage().size());
        assertEquals(expectedSessions, clientStack.getProvider().getSessionStorage().size());
    }
}