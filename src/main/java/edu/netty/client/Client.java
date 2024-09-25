package edu.netty.client;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import edu.netty.server.MessageProcessor;
import edu.netty.server.channel.MessageChannel;

import java.util.concurrent.ConcurrentHashMap;

import edu.netty.common.executor.MessageProcessorExecutor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
	private static String host = "localhost";
	private static int port = 8080;
	private static Channel channel;
	public static MessageProcessorExecutor executor = new MessageProcessorExecutor();
	
	public static void main(String[] args) throws Exception {
		NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                        	channel = socketChannel; 
                        	socketChannel.pipeline().addLast(new MobiusClientInitializer());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            sendMessage("Hi");
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
   }
    
    public static void sendMessage(String msg) {
    	ByteBuf message = new Message(MessageTypeEnum.ACK, msg).toByteBuf();
    	channel.writeAndFlush(message);
    }
}