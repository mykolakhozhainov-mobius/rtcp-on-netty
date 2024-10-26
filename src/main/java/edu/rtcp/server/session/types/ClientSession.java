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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientSession extends Session {
    private final Queue<RtcpBasePacket> lastSentMessages = new ConcurrentLinkedQueue<>();
    private final Queue<MessageTask> pendingTasks = new ConcurrentLinkedQueue<>();

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

        MessageTask initialRequestTask = new MessageTask(request) {
            @Override
            public void execute() {
                setSessionState(SessionStateEnum.WAITING);
                provider.getStack().getNetworkManager().sendMessage(message, port, callback);
            }
        };

        // Sending Sender Report (Session opening message)
        if (!this.lastSentMessages.isEmpty()) {
            this.pendingTasks.add(initialRequestTask);
        } else {
            this.sendMessageAsTask(initialRequestTask);
        }

        lastSentMessages.add(request);
    }

    public void sendTerminationRequest(Bye request, int port, AsyncCallback callback) {
        if (this.state == SessionStateEnum.CLOSED) {
            callback.onError(new RuntimeException("Client session can not send termination request cause it is already opened"));
            return;
        }

        // Sending Bye (Session closing message)
        MessageTask terminationTask = new MessageTask(request) {
            @Override
            public void execute() {
                setSessionState(SessionStateEnum.WAITING);
                provider.getStack().getNetworkManager().sendMessage(message, port, callback);
            }
        };

        if (!this.lastSentMessages.isEmpty()) {
            this.pendingTasks.add(terminationTask);
        } else {
            this.sendMessageAsTask(terminationTask);
        }

        lastSentMessages.add(request);
    }

    public void sendDataRequest(RtcpBasePacket request, int port, AsyncCallback callback) {
        if (this.state == SessionStateEnum.CLOSED) {
            callback.onError(new RuntimeException("Client session can not send data request cause session is closed"));
            return;
        }

        // Sending Bye (Session closing message)
        MessageTask dataTask = new MessageTask(request) {
            @Override
            public void execute() {
                setSessionState(SessionStateEnum.WAITING);
                provider.getStack().getNetworkManager().sendMessage(message, port, callback);
            }
        };

        if (!this.lastSentMessages.isEmpty()) {
            this.pendingTasks.add(dataTask);
        } else {
            this.sendMessageAsTask(dataTask);
        }

        lastSentMessages.add(request);
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

        RtcpBasePacket lastSentMessage = lastSentMessages.poll();

        if (lastSentMessage instanceof SenderReport && lastSentMessage.getHeader().getItemCount() == 0) {
            this.setSessionState(SessionStateEnum.OPEN);

            if (listener != null) {
                listener.onInitialAnswer(answer, this, callback);
            }
        } else if (lastSentMessage instanceof Bye) {
            this.setSessionState(SessionStateEnum.CLOSED);
            this.provider.getSessionStorage().remove(this);

            if (listener != null) {
                listener.onTerminationAnswer(answer, this, callback);
            }
        } else {
            if (listener != null) {
                listener.onDataAnswer(answer, this, callback);
            }
        }

        if (!this.pendingTasks.isEmpty()) {
            super.sendMessageAsTask(this.pendingTasks.poll());
        }
    }

    @Override
    public boolean isServer() {
        return false;
    }
}
