package edu.rtcp.server.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {
    private final Map<Integer, Session> sessionsMap = new ConcurrentHashMap<>();

    public Session get(int id) {
        return this.sessionsMap.get(id);
    }

    public void store(Session session) {
        sessionsMap.put(session.getId(), session);
    }

    public void remove(Session session) {
        sessionsMap.remove(session.getId());
    }
}
