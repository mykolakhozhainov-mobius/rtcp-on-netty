package edu.rtcp.server.session.types;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.executor.tasks.MessageTask;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ClientSession extends Session {
    public ClientSession(UUID id, Provider provider, InetSocketAddress remoteAddress) {
        this.id = id;
        this.state = SessionStateEnum.IDLE;
        this.provider = provider;
        this.remoteAddress = remoteAddress;
    }

    public void sendInitialRequest(Message request, InetSocketAddress address, AsyncCallback callback) {
        if (this.state != SessionStateEnum.IDLE) {
            callback.onError(new RuntimeException("Initial request can not be sent through already opened session"));
            return;
        }

        request.sessionId = this.id;

        ClientSession session = this;

        this.provider.getStack().getProcessor().executor.addTaskLast(new MessageTask() {
            @Override
            public String getId() {
                return "";
            }

            @Override
            public void execute() {
                session.setSessionState(SessionStateEnum.OPEN);

                provider.getStack().getNetworkManager().sendMessage(request, address, callback);
            }

            @Override
            public long getStartTime() {
                return 0;
            }
        });
    }
}
