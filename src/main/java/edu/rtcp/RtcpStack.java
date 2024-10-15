package edu.rtcp;

import edu.rtcp.server.executor.MessageExecutor;
import edu.rtcp.server.network.NetworkManager;
import edu.rtcp.server.network.processor.AbstractProcessor;
import edu.rtcp.server.network.processor.transport.StreamProcessor;
import edu.rtcp.server.provider.Provider;

public class RtcpStack {
    // Executor and it's constants ---------------------
    private final MessageExecutor messageExecutor;

    private static final int WORKERS_NUMBER = 4;
    private static final int TASK_INTERVAL = 100;

    // Message processing ------------------------------
    private final AbstractProcessor processor;

    // Networking --------------------------------------
    public final boolean isServer;

    private final NetworkManager networkManager;

    // Provider ----------------------------------------
    private Provider provider;

    public RtcpStack(boolean isServer) {
        this.isServer = isServer;

        this.messageExecutor = new MessageExecutor();
        this.messageExecutor.start(WORKERS_NUMBER, TASK_INTERVAL);

        this.processor = new StreamProcessor(8080, this);
        this.processor.start();

        this.networkManager = new NetworkManager(this);

        System.out.println("[STACK] Components initialized");
    }

    public void registerProvider(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return this.provider;
    }

    public AbstractProcessor getProcessor() {
        return this.processor;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public MessageExecutor getMessageExecutor() {
        return this.messageExecutor;
    }
}
