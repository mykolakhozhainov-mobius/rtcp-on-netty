package edu.netty.client;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import edu.netty.server.MessageProcessor;
import edu.netty.server.channel.MessageChannel;
import edu.netty.server.task.IdentifiedTask;

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
import com.mobius.software.common.dal.timers.Task;

public class Client {
	private static String host = "localhost";
	private static int port = 8080;
	private Channel channel;
	public MessageProcessorExecutor executor;

	public Client() {
		executor = new MessageProcessorExecutor();
	}

	public void start() throws InterruptedException {
		NioEventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) {
							channel = socketChannel;
							socketChannel.pipeline().addLast(new MobiusClientInitializer());
						}
					});
			ChannelFuture future = bootstrap.connect(host, port).sync();

			new Thread(() -> {
				try {
					channel.closeFuture().sync();
				} catch (InterruptedException e) {
					System.out.println(e);
				} finally {
					group.shutdownGracefully();
				}
			}).start();
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	public void sendMessage(String msg) {
		ByteBuf message = new Message(MessageTypeEnum.ACK, msg).toByteBuf();
		channel.writeAndFlush(message);

		executor.addTaskLast(new IdentifiedTask() {
			@Override
			public void execute() {
				channel.writeAndFlush(message);
			}

			@Override
			public long getStartTime() {
				return System.currentTimeMillis();
			}

			@Override
			public String getId() {
				return "Task-" + System.currentTimeMillis();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		Client cl1 = new Client();
		cl1.start();
		cl1.executor.start(8, 1000);
		for (int i = 0; i < 10; i++) {
			cl1.sendMessage("meme");
			Thread.sleep(100);
		}
	}
}