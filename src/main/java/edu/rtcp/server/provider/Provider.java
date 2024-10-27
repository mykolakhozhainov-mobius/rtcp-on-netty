package edu.rtcp.server.provider;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.rtcp.factory.PacketFactory;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.ReceiverReport;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.SessionFactory;
import edu.rtcp.server.session.SessionStorage;
import edu.rtcp.server.session.types.ServerSession;
import edu.rtcp.server.session.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Provider {
    private final RtcpStack stack;

    // Sessions handling -------------------------
    private final SessionStorage sessionStorage;
    private final SessionFactory sessionFactory = new SessionFactory(this);

    // Listeners ---------------------------------
    private ServerSessionListener serverListener;
    private ClientSessionListener clientListener;

    // Messages ----------------------------------
    private final PacketFactory packetFactory = new PacketFactory();

    public Provider(RtcpStack stack) {
        this.stack = stack;
        this.sessionStorage = new SessionStorage(stack);
    }

    public RtcpStack getStack() {
        return this.stack;
    }

    // Listeners ---------------------------------
    public ServerSessionListener getServerListener() {
        return this.serverListener;
    }

    public ClientSessionListener getClientListener() {
        return this.clientListener;
    }

    public void setServerListener(ServerSessionListener serverListener) {
        this.serverListener = serverListener;
    }

    public void setClientListener(ClientSessionListener clientListener) {
        this.clientListener = clientListener;
    }

    public PacketFactory getPacketFactory() {
        return this.packetFactory;
    }

    // Sessions handling -------------------------
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public SessionStorage getSessionStorage() {
        return this.sessionStorage;
    }

    // This session is created when there are no session specified and
    // Message request has come to onMessage() function
    // So, as a result, created session is Server session
    private Session createNewSession(RtcpBasePacket message) {
        int sessionId = message.getSSRC();

        return new ServerSession(sessionId, this);
    }

    // Event handling -----------------------------
    public void onMessage(RtcpBasePacket message, AsyncCallback callback) {
         int sessionId = message.getSSRC();

        boolean isAnswer = message instanceof ReceiverReport && message.getHeader().getItemCount() == 0;
        boolean isNewSession = false;

        Session session = sessionStorage.get(sessionId);
        if (session == null && !isAnswer) {
            session = this.createNewSession(message);
            this.sessionStorage.store(session);

            isNewSession = true;
        }

        if (session == null) {
            callback.onError(new RuntimeException("Session is not created"));
            return;
        }

        if (isAnswer) {
            session.processAnswer(message, callback);
        } else {
            session.processRequest(message, isNewSession, callback);
        }
    }
}
