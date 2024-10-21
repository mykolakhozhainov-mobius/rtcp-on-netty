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
        return new ServerSession(request.getHeader().getSSRC(), provider);
    }

    public ClientSession createClientSession(RtcpBasePacket request) {
        return new ClientSession(request.getHeader().getSSRC(), provider);
    }
}
