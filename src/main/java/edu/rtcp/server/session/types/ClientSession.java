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

        this.lastSentMessage = request;

        super.sendMessage(request, port, callback);
        this.setSessionState(SessionStateEnum.WAITING);
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
                setSessionState(SessionStateEnum.WAITING);
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
            System.out.println(this.state);
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
            listener.onDataAnswer(answer, this, callback);
        }
    }

    public void sendMessageAndWaitForAck(RtcpBasePacket packet, int port, AsyncCallback callback) {
        this.sendMessage(packet, port, callback);

        this.setSessionState(SessionStateEnum.WAITING);
    }

    @Override
    public boolean isServer() {
        return false;
    }
}
