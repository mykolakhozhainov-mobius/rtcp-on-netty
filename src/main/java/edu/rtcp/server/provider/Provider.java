package edu.rtcp.server.provider;

import edu.rtcp.common.message.Message;
import edu.rtcp.RtcpStack;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.provider.listeners.SessionListener;
import edu.rtcp.server.session.SessionFactory;
import edu.rtcp.server.session.SessionStorage;
import edu.rtcp.server.session.types.ServerSession;
import edu.rtcp.server.session.Session;

import java.util.UUID;

public class Provider {
    private final RtcpStack stack;
    private ServerSessionListener listener;

    private final SessionStorage sessionStorage = new SessionStorage();
    private final SessionFactory sessionFactory = new SessionFactory(this);

    public Provider(RtcpStack stack) {
        this.stack = stack;
    }

    public ServerSessionListener getListener() {
        return this.listener;
    }

    public RtcpStack getStack() {
        return this.stack;
    }

    public void setListener(ServerSessionListener listener) {
        this.listener = listener;
    }

    public SessionStorage getSessionStorage() {
        return this.sessionStorage;
    }

    private Session createNewSession(Message message) {
        return new ServerSession(UUID.randomUUID(), this, message.sender);
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
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
