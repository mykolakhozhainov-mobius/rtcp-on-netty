package edu.netty.server.session;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class Session {
    private static AtomicInteger counter = new AtomicInteger(0);

    public int id;

    private InetAddress from;
    private InetAddress to;

    public Session() {
        id = counter.incrementAndGet();
    }
}
