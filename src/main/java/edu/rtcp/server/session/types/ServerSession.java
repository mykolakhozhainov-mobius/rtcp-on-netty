package edu.rtcp.server.session.types;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.common.message.rtcp.packet.Bye;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.executor.tasks.MessageTask;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.provider.listeners.ServerSessionListener;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerSession extends Session {
    public static Logger logger = LogManager.getLogger(ServerSession.class);

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

                if (provider.getStack().isLogging) {
                    logger.info("ACK on initial in session {} sent", ServerSession.this.id);
                }
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

                if (provider.getStack().isLogging) {
                    logger.info("ACK on data in session {} sent", ServerSession.this.id);
                }
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

                if (provider.getStack().isLogging) {
                    logger.info("ACK on termination in session {} sent", ServerSession.this.id);
                }
            }
        });
    }

    @Override
    public void processRequest(RtcpBasePacket request, boolean isNewSession, AsyncCallback callback) {
        ServerSessionListener listener = this.provider.getServerListener();

        boolean isLogging = this.provider.getStack().isLogging;

        if (request instanceof Bye) {
            if (isLogging) logger.info("Message [Bye] is in process");

            if (this.state == SessionStateEnum.CLOSED) {
                callback.onError(new RuntimeException("Closed session can not be closed"));
                return;
            }

            if (listener != null) {
                listener.onTerminationRequest(request, this, callback);
            }
        } else if (isNewSession) {
            if (isLogging) logger.info("Message [SR] is in process");

            if (this.state == SessionStateEnum.OPEN) {
                callback.onError(new RuntimeException("Opened session can not be opened"));
                return;
            }

            if (listener != null) {
                listener.onInitialRequest(request, this, callback);
            }
        } else {
            if (isLogging) logger.info("Data message [APP, SD, RR] is in process");

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
