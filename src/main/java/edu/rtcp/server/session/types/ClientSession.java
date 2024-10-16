package edu.rtcp.server.session.types;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

import java.util.UUID;

public class ClientSession extends Session {
    private Message lastSentMessage;

    public ClientSession(UUID id, Provider provider) {
        this.id = id;
        this.state = SessionStateEnum.IDLE;
        this.provider = provider;
    }

    public void sendInitialRequest(Message request, AsyncCallback callback) {
        // TODO: Implement this method
    }

    public void sendTerminationRequest() {
        // TODO: Implement this method
    }

    @Override
    public void processRequest(Message request, AsyncCallback callback) {
        // Here will be some logic if client will process any requests
    }

    @Override
    public void processAnswer(Message answer, AsyncCallback callback) {
        ClientSessionListener listener = this.provider.getClientListener();

        // TODO: Implement answer processing
        // We can also validate the message that incomes by checking it
        // To the type of the lastSentMessage

        if (listener != null) {
            // If answer message is came after we sent initial message
            listener.onInitialAnswer(answer, this, callback);

            // If answer message is came after we sent termination message
            listener.onTerminationAnswer(answer, this, callback);

            // If answer message is came after we sent data
            listener.onDataRequest(answer, this, callback);
        }
    }

    @Override
    public boolean isServer() {
        return false;
    }
}
