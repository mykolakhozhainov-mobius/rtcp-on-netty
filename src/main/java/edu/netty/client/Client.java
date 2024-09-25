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
	private String host;
	private int port;
	private Channel channel;
	public MessageProcessorExecutor executor = new MessageProcessorExecutor();

	public Client(String host, int port) {
      executor = new MessageProcessorExecutor();
      this.host = host;
      this.port = port;
	}
	
	public void start() throws InterruptedException {
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
            
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
	}
	
	public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.start();
//        client.executor.start(8, 1000);
        for (int i=0; i<10; i++) {
        	Thread.sleep(100);
        	client.sendMessage(client.channel, "Initial");
        }
        client.sendMessage(client.channel, "Initial");
   }
    
    public void sendMessage(Channel channel,String msg) {
    	ByteBuf message = new Message(MessageTypeEnum.ACK, msg).toByteBuf();
    	channel.writeAndFlush(message);
    }
}