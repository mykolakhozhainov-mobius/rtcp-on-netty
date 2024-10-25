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
    	ServerSession session = new ServerSession(request.getSSRC(), provider);
    	provider.getSessionStorage().store(session);
    	
    	return session;
    }

    public ClientSession createClientSession(RtcpBasePacket request) {
        ClientSession session = new ClientSession(request.getSSRC(), provider);
    	provider.getSessionStorage().store(session);
    	
    	return session;
    }
}
