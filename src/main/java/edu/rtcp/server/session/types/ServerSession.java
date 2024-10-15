package edu.rtcp.server.session.types;

import edu.rtcp.server.provider.Provider;
import edu.rtcp.server.session.Session;
import edu.rtcp.server.session.SessionStateEnum;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ServerSession extends Session {
    public ServerSession(UUID id, Provider provider, InetSocketAddress remoteAddress) {
        this.id = id;
        this.state = SessionStateEnum.IDLE;
        this.provider = provider;
        this.remoteAddress = remoteAddress;
    }
}
