package edu.rtcp.server.session;

import edu.rtcp.RtcpStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {
    public static Logger logger = LogManager.getLogger(SessionStorage.class);

    private final RtcpStack stack;

    public SessionStorage(RtcpStack stack) {
        this.stack = stack;
    }

    private final Map<Integer, Session> sessionsMap = new ConcurrentHashMap<>();

    public Session get(int id) {
        return this.sessionsMap.get(id);
    }

    public void store(Session session) {
        sessionsMap.put(session.getId(), session);

        if (this.stack.isLogging) {
            logger.info("Session {} is stored", session.getId());
        }
    }

    public void remove(Session session) {
        sessionsMap.remove(session.getId());

        if (this.stack.isLogging) {
            logger.info("Session {} is removed", session.getId());
        }
    }

    public int size() { return this.sessionsMap.size(); }
}
