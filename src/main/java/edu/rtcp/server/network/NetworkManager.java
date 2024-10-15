package edu.rtcp.server.network;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.processor.transport.StreamProcessor;
import io.netty.channel.Channel;
import io.netty.channel.epoll.EpollServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {
    private NetworkListener networkListener;
    private RtcpStack stack;

    public NetworkManager(RtcpStack stack) {
        this.stack = stack;
    }

    public NetworkListener getNetworkListener() {
        return this.networkListener;
    }

    public void setNetworkListener(NetworkListener listener) {
        this.networkListener = listener;
    }

    public void sendMessage(Message message, InetSocketAddress address, AsyncCallback callback) {
        //System.out.println(stack.getProcessor());
        Channel channel = ((StreamProcessor) stack.getProcessor()).channel;
        System.out.println(channel.isOpen());
        //System.out.println(channel);

        channel.writeAndFlush(message.toByteBuf());
    }
}
