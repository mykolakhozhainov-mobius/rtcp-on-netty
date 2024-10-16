package edu.rtcp.server.provider;

import edu.rtcp.common.message.Message;
import edu.rtcp.RtcpStack;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.SessionFactory;
import edu.rtcp.server.session.SessionStorage;
import edu.rtcp.server.session.types.ServerSession;
import edu.rtcp.server.session.Session;

import java.util.UUID;

public class Provider {
    private final RtcpStack stack;

    // Sessions handling -------------------------
    private final SessionStorage sessionStorage = new SessionStorage();
    private final SessionFactory sessionFactory = new SessionFactory(this);

    // Listeners ---------------------------------
    private ServerSessionListener serverListener;
    private ClientSessionListener clientListener;

    public Provider(RtcpStack stack) {
        this.stack = stack;
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
    private Session createNewSession(Message message) {
        UUID id = message.sessionId == null ?
                UUID.randomUUID() :
                message.sessionId;

        return new ServerSession(id, this);
    }

    public void onMessage(Message message, AsyncCallback callback) {
        UUID sessionId = message.sessionId;
        if (sessionId == null) {
            callback.onError(new RuntimeException("Session ID is null"));
            return;
        }

        Session session = sessionStorage.get(sessionId);
        if (session == null) { //&& message instanceof Request) {
            session = this.createNewSession(message);
            this.sessionStorage.store(session);
        }

        session.processRequest(message, callback);
//        if (message instanceof Request) {
//            session.processRequest();
//        } else {
//            session.processResponce();
//        }
    }
}
