package edu.rtcp.server.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionStorage {
    private final Map<UUID, Session> sessionsMap = new HashMap<UUID, Session>();

    public Session get(UUID id) {
        return this.sessionsMap.get(id);
    }

    public void store(Session session) {
        sessionsMap.put(session.getId(), session);
    }

    public void remove(Session session) {
        sessionsMap.remove(session.getId());
    }
}
