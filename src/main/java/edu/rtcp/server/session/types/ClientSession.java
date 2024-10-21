package edu.rtcp.server.session.types;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.executor.tasks.MessageTask;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

public class ClientSession extends Session {
    boolean isFirstMessage = true;
    private RtcpBasePacket lastSentMessage;

    public ClientSession(int id, Provider provider) {
        this.id = id;
        this.state = SessionStateEnum.IDLE;
        this.provider = provider;
    }

    public void sendInitialRequest(RtcpBasePacket request, int port, AsyncCallback callback) {
        if (this.state != SessionStateEnum.IDLE) {
            callback.onError(new RuntimeException("Client session can not send initial request cause it is already opened"));
            return;
        }

        this.provider.getStack().getMessageExecutor().addTaskFirst(new MessageTask() {
            @Override
            public void execute() {
                lastSentMessage = request;

                sendMessage(request, port, callback);
            }
        });
    }

    public void sendTerminationRequest(Bye request, int port, AsyncCallback callback) {
        if (this.state != SessionStateEnum.OPEN) {
            callback.onError(new RuntimeException("Client session can not send termination request cause it is already opened"));
            return;
        }

        this.provider.getStack().getMessageExecutor().addTaskFirst(new MessageTask() {
            @Override
            public void execute() {
                lastSentMessage = request;

                sendMessage(request, port, callback);
            }
        });
    }

    @Override
    public void processRequest(RtcpBasePacket request, boolean isNewSession, AsyncCallback callback) {
        // Here will be some logic if client will process any requests
    }

    @Override
    public void processAnswer(RtcpBasePacket answer, AsyncCallback callback) {
        ClientSessionListener listener = this.provider.getClientListener();

        // TODO: Rework message types
        // Data messages are ignored

        if (isFirstMessage) {
            if (this.state != SessionStateEnum.IDLE) {
                callback.onError(new RuntimeException("Init session answer gotten by already opened session"));
                return;
            }

            if (listener != null) {
                listener.onInitialAnswer(answer, this, callback);
            }
        } else if (answer instanceof Bye) {
            if (this.state != SessionStateEnum.OPEN) {
                callback.onError(new RuntimeException("Session can not be closed as it is not opened"));
                return;
            }

            if (listener != null) {
                listener.onTerminationAnswer(answer, this, callback);
            }
        }

        isFirstMessage = false;
    }

    @Override
    public boolean isServer() {
        return false;
    }
}
