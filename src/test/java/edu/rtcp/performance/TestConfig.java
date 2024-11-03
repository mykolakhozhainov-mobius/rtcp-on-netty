package edu.rtcp.performance;

import edu.rtcp.common.TransportEnum;

public class TestConfig {
    public static final int SESSION_NUMBER = 10000;
    public static final int INIT_TIME = 5;

    // Networking options ------------------------------------------
    public static final String ADDRESS = "127.0.0.1";
    public static final TransportEnum TRANSPORT = TransportEnum.UDP;
    public static final int CONNECTIONS_NUMBER = 4;

    // Performance options ------------------------------------------
    public static final int IDLE_TIMEOUT = 1000;
    public static final int RESPONSE_TIMEOUT = 10000;
    public static final int THREAD_POOL_SIZE = 4;

    public static final boolean LOGGING = false;
}
