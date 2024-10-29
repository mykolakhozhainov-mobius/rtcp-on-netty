package edu.rtcp.examples;

import edu.rtcp.common.TransportEnum;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Configuration {
    public static final AtomicInteger serverReceived = new AtomicInteger(0);
    public static final AtomicInteger serverSent = new AtomicInteger(0);

    public static final AtomicInteger clientSent = new AtomicInteger(0);
    public static final AtomicInteger clientAcks = new AtomicInteger(0);

    public static final int SESSION_NUMBER = 10000;

    public static final int TIME_LIMIT = 5000;
    public static final int THREAD_POOL_SIZE = 8;

    public static final TransportEnum TRANSPORT = TransportEnum.UDP;
    public static final boolean LOGGING = false;

    private static final HashSet<Integer> usedIds = new HashSet<>();

    public static int generateId() {
        int id = Math.abs(new Random().nextInt());

        while (usedIds.contains(id)) {
            id = Math.abs(new Random().nextInt());
        }

        usedIds.add(id);

        return id;
    }
}
