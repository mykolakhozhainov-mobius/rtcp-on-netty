package edu.rtcp.server.session;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.session.types.ClientSession;
import edu.rtcp.server.session.types.ServerSession;

public class SessionFactory {
    private final Provider provider;

    public SessionFactory(Provider provider) {
        this.provider = provider;
    }

    public ServerSession createServerSession(RtcpBasePacket request) {
        ServerSession serverSession = new ServerSession(request.getSSRC(), provider);

        this.provider.getSessionStorage().store(serverSession);

        return serverSession;
    }

    public ClientSession createClientSession(RtcpBasePacket request) {
        ClientSession clientSession = new ClientSession(request.getSSRC(), provider);

        this.provider.getSessionStorage().store(clientSession);

        return clientSession;
    }
}
