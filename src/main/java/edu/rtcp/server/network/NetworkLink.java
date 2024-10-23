package edu.rtcp.server.network;

import io.netty.channel.Channel;

import java.net.InetAddress;

public class NetworkLink {
    private String linkId;
    private InetAddress remoteAddress;
    private int remotePort;
    private InetAddress localAddress;
    private int localPort;
    private Channel channel;
    NetworkManager networkManager;

    public NetworkLink(String linkId, InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort, NetworkManager networkManager) {
        this.linkId = linkId;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.localAddress = localAddress;
        this.localPort = localPort;

        this.networkManager = networkManager;
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