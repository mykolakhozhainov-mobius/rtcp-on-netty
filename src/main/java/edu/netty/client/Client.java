package edu.netty.client;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import edu.netty.server.task.IdentifiedTask;
import java.util.UUID;

import edu.netty.common.executor.MessageProcessorExecutor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
	private static String host = "localhost";
	private static int port = 8080;
	private Channel channel;
	public MessageProcessorExecutor executor;
    private UUID sessionId = new UUID(1, 2);

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

	public void sendMessage(Message message) throws InterruptedException {
		//Thread.sleep(2000);
		channel.writeAndFlush(message);

		executor.addTaskLast(new IdentifiedTask() {
			@Override
			public void execute() {
				channel.writeAndFlush(message.toByteBuf());
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
		// ATTENTION: workers numbers is 1
		cl1.executor.start(1, 1000);

		Message begin = new Message(MessageTypeEnum.OPEN, "Create session");
		UUID session = begin.sessionId;

		cl1.sendMessage(begin);
		cl1.sendMessage(new Message(session, MessageTypeEnum.DATA, "1"));
		cl1.sendMessage(new Message(session, MessageTypeEnum.DATA, "2"));
		cl1.sendMessage(new Message(session, MessageTypeEnum.DATA, "3"));

		cl1.sendMessage(new Message("Not from session"));
	}
}