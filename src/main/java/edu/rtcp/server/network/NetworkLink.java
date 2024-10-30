package edu.rtcp.server.network;

import java.net.InetAddress;

import io.netty.channel.Channel;

public class NetworkLink {
    private final String linkId;

    private final InetAddress remoteAddress;
    private final int remotePort;

    private final InetAddress localAddress;
    private final int localPort;

    private Channel channel;
    private final NetworkManager networkManager;

    public NetworkLink(String linkId, InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, NetworkManager networkManager) {
        this.linkId = linkId;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.localAddress = localAddress;
        this.localPort = localPort;

        this.networkManager = networkManager;
    }
    
    

    public String getLinkId() {
		return linkId;
	}



	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}



	public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public int getLocalPort() {
        return localPort;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }
}