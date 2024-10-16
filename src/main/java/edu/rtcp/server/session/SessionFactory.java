package edu.rtcp.server.session;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.session.types.ClientSession;
import edu.rtcp.server.session.types.ServerSession;

public class SessionFactory {
    private final Provider provider;

    public SessionFactory(Provider provider) {
        this.provider = provider;
    }

    public ServerSession createServerSession(Message request) {
        return new ServerSession(request.sessionId, provider);
    }

    public ClientSession createClientSession(Message request) {
        return new ClientSession(request.sessionId, provider);
    }
}
