package edu.rtcp.server.session.types;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.executor.tasks.MessageTask;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

public class ServerSession extends Session {
    public ServerSession(int id, Provider provider) {
        this.id = id;
        this.state = SessionStateEnum.IDLE;
        this.provider = provider;
    }

    public void sendInitialAnswer(RtcpBasePacket answer, AsyncCallback callback) {
        if (this.state != SessionStateEnum.IDLE) {
            callback.onError(new RuntimeException("Can not send initial answer cause session is already open or closed"));
            return;
        }

        this.provider.getStack().getMessageExecutor().addTaskLast(new MessageTask() {
            @Override
            public void execute() {
                setSessionState(SessionStateEnum.OPEN);

                // TODO: how to determine originator of the message
                // provider.getStack().getNetworkManager().sendMessage(answer, callback);
            }
        });
    }

    public void sendTerminationAnswer(RtcpBasePacket answer, AsyncCallback callback) {
        if (this.state != SessionStateEnum.OPEN) {
            callback.onError(new RuntimeException("Can not terminate not opened session"));
            return;
        }

        ServerSession session = this;

        this.provider.getStack().getMessageExecutor().addTaskLast(new MessageTask() {
            @Override
            public void execute() {
                setSessionState(SessionStateEnum.CLOSED);
                provider.getSessionStorage().remove(session);

                // TODO: how to determine originator of the message
                // provider.getStack().getNetworkManager().sendMessage(answer, callback);
            }
        });
    }

    @Override
    public void processRequest(RtcpBasePacket request, boolean isNewSession, AsyncCallback callback) {
        ServerSessionListener listener = this.provider.getServerListener();

        if (request instanceof Bye) {
            if (this.state == SessionStateEnum.CLOSED) {
                callback.onError(new RuntimeException("Closed session can not be closed"));
                return;
            }

            if (listener != null) {
                listener.onTerminationRequest(request, this, callback);
            }
        } else if (isNewSession) {
            if (this.state == SessionStateEnum.OPEN) {
                callback.onError(new RuntimeException("Opened session can not be opened"));
                return;
            }

            if (listener != null) {
                listener.onInitialRequest(request, this, callback);
            }
        } else {
            if (this.state == SessionStateEnum.CLOSED) {
                callback.onError(new RuntimeException("Closed session can not handle data"));
                return;
            }

            if (listener != null) {
                listener.onDataRequest(request, this, callback);
            }
        }
    }

    @Override
    public void processAnswer(RtcpBasePacket answer, AsyncCallback callback) {
        // Here will be some logic if message will send any requests
        // To the client and wanting to process the answers
    }

    @Override
    public boolean isServer() {
        return true;
    }
}
