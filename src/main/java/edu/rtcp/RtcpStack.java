package edu.rtcp;

import edu.rtcp.common.TransportEnum;
import edu.rtcp.server.executor.MessageExecutor;
import edu.rtcp.server.network.NetworkManager;
import edu.rtcp.server.provider.Provider;

public class RtcpStack {
    // Executor and it's constants ---------------------
    private final MessageExecutor messageExecutor;
    private final int threadPoolSize;

    private static final int WORKERS_NUMBER = 16;
    private static final int TASK_INTERVAL = 1000;

    // Message processing ------------------------------
//    private final AbstractProcessor processor;

    // Networking --------------------------------------
    public final boolean isServer;
    public final TransportEnum transport;

    private final NetworkManager networkManager;

    // Provider ----------------------------------------
    private Provider provider;

    public RtcpStack(int threadPoolSize, boolean isServer, TransportEnum transport) {
        this.isServer = isServer;

        this.messageExecutor = new MessageExecutor();
        this.messageExecutor.start(WORKERS_NUMBER, TASK_INTERVAL);

        this.threadPoolSize = threadPoolSize;

//        this.processor = new StreamProcessor(8080, this);
//        this.processor.start();

        this.networkManager = new NetworkManager(this);
        this.transport = transport;

        System.out.println("[STACK] Components initialized");
    }

    public void registerProvider(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return this.provider;
    }

    public void stop()
    {
        networkManager.stop();
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }

//    public AbstractProcessor getProcessor() {
//        return this.processor;
//    }

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
