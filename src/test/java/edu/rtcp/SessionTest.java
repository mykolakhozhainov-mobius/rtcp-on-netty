package edu.rtcp;

import edu.rtcp.common.TransportEnum;
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

import static org.junit.Assert.*;

/* NOTE:
* Session's method processRequest is public, but
* It is not recommended to use it in your custom application
* Cause it in encapsulated into Provider process method
* HERE IT IS USED JUST FOR TESTING
*/
public class SessionTest {
    private RtcpStack stack;
    private ServerSession session;

    private final AtomicInteger inits = new AtomicInteger(0);
    private final AtomicInteger datas = new AtomicInteger(0);
    private final AtomicInteger terms = new AtomicInteger(0);

    @Before
    public void setupStack() {
        this.stack = new RtcpStack(4, true, TransportEnum.TCP);
        stack.setProvider(new Provider(stack));

        stack.getProvider().setServerListener(new ServerSessionListener() {
            @Override
            public void onInitialRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                inits.incrementAndGet();
                session.setSessionState(SessionStateEnum.OPEN);
            }

            @Override
            public void onTerminationRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                terms.incrementAndGet();
                session.setSessionState(SessionStateEnum.CLOSED);
            }

            @Override
            public void onDataRequest(RtcpBasePacket request, Session session, AsyncCallback callback) {
                datas.incrementAndGet();
            }
        });
    }

    private void setupSession() {
        PacketFactory factory = this.stack.getProvider().getPacketFactory();

        SenderReport openPacket = factory.createSenderReport(
                (byte) 1,
                0,
                null,
                null
        );

        this.session = this.stack.getProvider()
                .getSessionFactory()
                .createServerSession(openPacket);
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
    public void testInitialization() {
        if (this.session == null) {
            this.setupSession();
        }

        // Test: if session calling onInitialRequest after isNewSession param is defined as true
        this.session.processRequest(null, true, null);

        assertEquals(1, this.inits.get());
        assertEquals(SessionStateEnum.OPEN, this.session.getSessionState());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRepetitiveInitialization() {
        // Preconditions: session is already opened
        if (this.session == null) {
            this.setupSession();
            this.session.processRequest(null, true, null);
        }

        AsyncCallback callback = new AsyncCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                throw new IllegalArgumentException(e);
            }
        };

        this.session.processRequest(null, true, callback);
    }

    @Test
    public void testDataHandling() {
        if (this.session == null) {
            this.setupSession();
            this.session.processRequest(null, true, null);
        }

        // Test: isNewSession param is false + not Bye message => just data
        this.session.processRequest(null, false, null);

        assertEquals(1, this.datas.get());

        this.session.processRequest(null, false, null);
        this.session.processRequest(null, false, null);
        this.session.processRequest(null, false, null);

        assertEquals(4, this.datas.get());
        assertEquals(SessionStateEnum.OPEN, this.session.getSessionState());
    }

    @Test
    public void testErrorHandling() {
        if (this.session == null) {
            this.setupSession();
            this.session.processRequest(null, true, null);
        }

        final AtomicInteger errors = new AtomicInteger(0);
        // Test: if data handling works fine and throws errors
        this.session.processRequest(null, true, new AsyncCallback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onError(Exception e) {
                errors.incrementAndGet();
            }
        });

        assertEquals(1, errors.get());
    }

    @Test
    public void testTermination() {
        if (this.session == null) {
            this.setupSession();
            this.session.processRequest(null, true, null);
        }

        PacketFactory factory = this.stack.getProvider().getPacketFactory();

        Bye byePacket = factory.createBye(
                (byte) 0,
                0,
                "Just for test"
        );


        this.session.processRequest(byePacket, false, null);

        assertEquals(1, this.terms.get());
    }
}
