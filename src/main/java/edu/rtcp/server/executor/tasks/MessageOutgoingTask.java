package edu.rtcp.server.executor.tasks;

import java.net.InetSocketAddress;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

public class MessageOutgoingTask extends MessageTask {
    private final Session session;
    private final InetSocketAddress address;
    private final AsyncCallback callback;

    public MessageOutgoingTask(Session session, RtcpBasePacket message, InetSocketAddress address, AsyncCallback callback) {
        super(message);

        this.session = session;
        this.address = address;
        this.callback = callback;
    }

    @Override
    public void execute() {
        this.session.setSessionState(SessionStateEnum.WAITING);

        this.session.getProvider()
                .getStack()
                .getNetworkManager()
                .sendMessage(message, address, callback);
    }
}
