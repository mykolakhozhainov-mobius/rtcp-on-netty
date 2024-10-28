package edu.rtcp;

import edu.rtcp.common.TransportEnum;
import edu.rtcp.server.executor.MessageExecutor;
import edu.rtcp.server.network.NetworkManager;
import edu.rtcp.server.provider.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RtcpStack {
    public final boolean isLogging;
    public static Logger logger = LogManager.getLogger(RtcpStack.class);

    // Executor and it's constants ---------------------
    private final MessageExecutor messageExecutor;
    private final int threadPoolSize;

    private static final int WORKERS_NUMBER = 16;
    private static final int TASK_INTERVAL = 100;

    // Message processing ------------------------------
//    private final AbstractProcessor processor;

    // Networking --------------------------------------
    public final boolean isServer;
    public final TransportEnum transport;

    private final NetworkManager networkManager;

    // Provider ----------------------------------------
    private Provider provider;

    public RtcpStack(int threadPoolSize, boolean isServer, TransportEnum transport, boolean isLogging) {
        this.isServer = isServer;

        this.messageExecutor = new MessageExecutor();
        this.messageExecutor.start(WORKERS_NUMBER, TASK_INTERVAL);

        this.threadPoolSize = threadPoolSize;

        this.networkManager = new NetworkManager(this);
        this.transport = transport;

        this.isLogging = isLogging;

        if (this.isLogging) {
            logger.info("New {} stack [{}] initialized", isServer ? "server" : "client", this.transport);
        }
    }

    public void registerProvider(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return this.provider;
    }

    public void stop() {
        this.networkManager.stop();
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public MessageExecutor getMessageExecutor() {
        return this.messageExecutor;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }
}
