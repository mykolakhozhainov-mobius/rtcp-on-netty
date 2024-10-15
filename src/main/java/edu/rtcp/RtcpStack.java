package edu.rtcp;

import edu.rtcp.server.network.NetworkManager;
import edu.rtcp.server.network.processor.AbstractProcessor;
import edu.rtcp.server.provider.Provider;

public class RtcpStack {
    private Provider provider;
    private AbstractProcessor processor;
    private final NetworkManager networkManager = new NetworkManager(this);

    public RtcpStack() {}

    public RtcpStack(Provider provider, AbstractProcessor processor) {
        this.provider = provider;
        this.processor = processor;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Provider getProvider() {
        return this.provider;
    }

    public AbstractProcessor getProcessor() {
        return this.processor;
    }

    public void setProcessor(AbstractProcessor processor) {
        this.processor = processor;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }
}
