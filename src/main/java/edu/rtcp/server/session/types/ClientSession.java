package edu.rtcp.server.session.types;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.executor.tasks.MessageTask;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ClientSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

public class ClientSession extends Session {
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
                setSessionState(SessionStateEnum.WAITING);

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
                setSessionState(SessionStateEnum.WAITING);

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

        if (this.state != SessionStateEnum.WAITING) {
            callback.onError(new RuntimeException("ACK response received while session status is not WAITING"));
            return;
        }

        if (lastSentMessage instanceof SenderReport && lastSentMessage.getHeader().getItemCount() == 0) {
            if (listener != null) {
                listener.onInitialAnswer(answer, this, callback);
            }
        } else if (lastSentMessage instanceof Bye) {
            if (listener != null) {
                listener.onTerminationAnswer(answer, this, callback);
            }
        } else {
            listener.onDataRequest(answer, this, callback);
        }
    }

    @Override
    public boolean isServer() {
        return false;
    }
}
