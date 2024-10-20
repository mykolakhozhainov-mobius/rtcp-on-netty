package edu.rtcp;

import edu.rtcp.common.message.rtcp.factory.PacketFactory;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;
import edu.rtcp.server.session.types.ServerSession;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionTest {
    private RtcpStack stack;

    private final AtomicInteger initialCounter = new AtomicInteger(0);
    private final AtomicInteger dataCounter = new AtomicInteger(0);
    private final AtomicInteger terminationCounter = new AtomicInteger(0);

    @Before
    public void setupStack() {
        this.stack = new RtcpStack();
        stack.setProvider(new Provider(stack));

        stack.getProvider().setServerListener(new ServerSessionListener() {
            @Override
            public void onInitialRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                initialCounter.incrementAndGet();
                session.setSessionState(SessionStateEnum.OPEN);
            }

            @Override
            public void onTerminationRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                terminationCounter.incrementAndGet();
                session.setSessionState(SessionStateEnum.CLOSED);
            }

            @Override
            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                dataCounter.incrementAndGet();
            }
        });
    }

    @Test
    public void testServerSession() {
        // Test: is session's state is IDLE after creation
        ServerSession serverSession = new ServerSession(0, null);
        assertEquals(SessionStateEnum.IDLE, serverSession.getSessionState());

        // Test: is session is server session
        assertTrue(serverSession.isServer());
    }

    @Test
    public void testServerSessionInitialization() {
        PacketFactory factory = this.stack.getProvider().getPacketFactory();

        SenderReport openPacket = factory.createSenderReport(
                (short) 1,
                false,
                (short) 1,
                PacketTypeEnum.SENDER_REPORT,
                10,
                1,
                0,
                0,
                0,
                0,
                0,
                null
        );

        ServerSession serverSession = this.stack.getProvider().getSessionFactory().createServerSession(openPacket);

        int initialBeforeTest = initialCounter.get();
        int dataBeforeTest = dataCounter.get();
        int terminationBeforeTest = terminationCounter.get();

        // Test: if session calling onInitialRequest after isNewSession param is defined as true
        serverSession.processRequest(null, true, null);

        assertEquals(initialBeforeTest + 1, this.initialCounter.get());
        assertEquals(SessionStateEnum.OPEN, serverSession.getSessionState());

        // Test: if session calling onData after no isNewSession and Bye was not passed
        serverSession.processRequest(null, false, null);

        assertEquals(dataBeforeTest + 1, this.dataCounter.get());
        assertEquals(SessionStateEnum.OPEN, serverSession.getSessionState());

        final AtomicInteger errorsCounter = new AtomicInteger(0);

        // Test: if data handling works fine and throws errors
        serverSession.processRequest(null, true, new AsyncCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                errorsCounter.incrementAndGet();
            }
        });

        assertEquals(1, errorsCounter.get());

        // TODO: Rework test cases
        // Add Bye message testing
    }
}
