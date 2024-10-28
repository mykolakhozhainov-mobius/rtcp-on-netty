package edu.rtcp.server.executor.tasks;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

public class MessageOutgoingTask extends MessageTask {
    private final Session session;
    private final int port;
    private final AsyncCallback callback;

    public MessageOutgoingTask(Session session, RtcpBasePacket message, int port, AsyncCallback callback) {
        super(message);

        this.session = session;
        this.port = port;
        this.callback = callback;
    }

    @Override
    public void execute() {
        this.session.setSessionState(SessionStateEnum.WAITING);

        this.session.getProvider()
                .getStack()
                .getNetworkManager()
                .sendMessage(message, port, callback);
    }
}
