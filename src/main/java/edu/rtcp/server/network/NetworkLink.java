package edu.rtcp.server.network;

import java.net.InetSocketAddress;
import io.netty.channel.Channel;

public class NetworkLink {
    private final String linkId;

    private final InetSocketAddress remoteAddress;

    private final InetSocketAddress localAddress;

    private Channel channel;

    public NetworkLink(String linkId, InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
        this.linkId = linkId;
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}