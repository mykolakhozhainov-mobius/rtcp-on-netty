package edu.rtcp.server.session.types;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

import java.util.UUID;

public class ServerSession extends Session {
    public ServerSession(UUID id, Provider provider) {
        this.id = id;
        this.state = SessionStateEnum.IDLE;
        this.provider = provider;
    }

    public void sendInitialAnswer() {
        // TODO: Implement this method
    }

    public void sendTerminationAnswer() {
        // TODO: Implement this method
    }

    @Override
    public void processRequest(Message request, AsyncCallback callback) {
        ServerSessionListener listener = this.provider.getServerListener();

        // TODO: Implement request processing
        // If it is Open Session message, then sendInitialAnswer()
        // If it is Close Session message, then sendTerminationAnswer()

        if (listener != null) {
            // If message type is Open Session
            listener.onInitialRequest(request, this, callback);

            // If message type is Close Session
            listener.onTerminationRequest(request, this, callback);

            // If message is just data
            listener.onDataRequest(request, this, callback);
        }
    }

    @Override
    public void processAnswer(Message answer, AsyncCallback callback) {
        // Here will be some logic if message will send any requests
        // To the client and wanting to process the answers
    }

    @Override
    public boolean isServer() {
        return true;
    }
}
