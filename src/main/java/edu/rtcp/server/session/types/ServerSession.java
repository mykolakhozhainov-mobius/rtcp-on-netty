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

    public void sendInitialAnswer(RtcpBasePacket answer, int port, AsyncCallback callback) {
        if (this.state != SessionStateEnum.IDLE) {
            callback.onError(new RuntimeException("Can not send initial answer cause session is already open or closed"));
            return;
        }

        super.sendMessageAsTask(new MessageTask(answer) {
            @Override
            public void execute() {
                setSessionState(SessionStateEnum.OPEN);
                provider.getStack().getNetworkManager().sendMessage(answer, port, callback);

                System.out.println("[SERVER-SESSION] ACK (RR) on initial message is sent");
            }
        });
    }

    public void sendDataAnswer(RtcpBasePacket answer, int port, AsyncCallback callback) {
        if (this.state != SessionStateEnum.OPEN) {
            callback.onError(new RuntimeException("Can not send data answer cause session is idle, waiting or closed"));
            return;
        }

        super.sendMessageAsTask(new MessageTask(answer) {
            @Override
            public void execute() {
                provider.getStack().getNetworkManager().sendMessage(answer, port, callback);

                System.out.println("[SERVER-SESSION] ACK (RR) on data message is sent");
            }
        });
    }

    public void sendTerminationAnswer(RtcpBasePacket answer, int port, AsyncCallback callback) {
        if (this.state != SessionStateEnum.OPEN) {
            callback.onError(new RuntimeException("Can not terminate not opened session"));
            return;
        }

        super.sendMessageAsTask(new MessageTask(answer) {
            @Override
            public void execute() {
                setSessionState(SessionStateEnum.CLOSED);
                provider.getSessionStorage().remove(ServerSession.this);

                provider.getStack().getNetworkManager().sendMessage(answer, port, callback);

                System.out.println("[SERVER-SESSION] ACK (RR) on termination message is sent");
            }
        });
    }

    @Override
    public void processRequest(RtcpBasePacket request, boolean isNewSession, AsyncCallback callback) {
        ServerSessionListener listener = this.provider.getServerListener();

        if (request instanceof Bye) {
            System.out.println("[SERVER-SESSION] Bye message received");
            if (this.state == SessionStateEnum.CLOSED) {
                callback.onError(new RuntimeException("Closed session can not be closed"));
                return;
            }

            if (listener != null) {
                listener.onTerminationRequest(request, this, callback);
            }
        } else if (isNewSession) {
            System.out.println("[SERVER-SESSION] SR (Open) message received");
            if (this.state == SessionStateEnum.OPEN) {
                callback.onError(new RuntimeException("Opened session can not be opened"));
                return;
            }

            if (listener != null) {
                listener.onInitialRequest(request, this, callback);
            }
        } else {
            System.out.println("[SERVER-SESSION] Data message received");
            if (this.state == SessionStateEnum.IDLE) {
                callback.onError(new RuntimeException("Unknown message type is passed to session"));
                return;
            } else if (state == SessionStateEnum.WAITING) {
                callback.onError(new RuntimeException("Data packet received while session is waiting for ACK"));
                return;
            } else if (this.state == SessionStateEnum.CLOSED) {
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
