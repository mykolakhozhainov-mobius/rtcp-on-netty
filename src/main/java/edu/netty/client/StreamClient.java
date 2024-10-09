package edu.netty.client;

import edu.netty.client.handlers.ClientInitializer;
import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import java.util.UUID;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class StreamClient extends AbstractClient {
	public StreamClient(int port) {
		this.port = port;
	}

	@Override
	public void start() {
		NioEventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) {
							channel = socketChannel;
							socketChannel.pipeline().addLast(new ClientInitializer(sessions));
						}
					});
			ChannelFuture future = bootstrap.connect(host, port).sync();
			if (!future.isSuccess()) {
				System.out.println("Channel is not initialized");
				System.out.println(future.cause());
				return;
			}

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

	public static void main(String[] args) {
		StreamClient streamClient = new StreamClient(8080);
		streamClient.start();
		streamClient.executor.start(8, 10);

		final int SESSIONS = 10;
		final int MESSAGES = 100;

		for (int i = 0; i < SESSIONS; i++) { streamClient.createSession(UUID.randomUUID()); }

		streamClient.sessions.values().forEach(session -> {
			session.addMessageTask(
					new Message(session.id, MessageTypeEnum.OPEN, "Open new session " + session.id),
                    (callSession, message) -> callSession.channel.writeAndFlush(message.toByteBuf())
            );

			for (int i = 0; i < MESSAGES; i++) {
				session.addMessageTask(
					new Message(session.id, MessageTypeEnum.DATA, "Message from client #" + i),
					(callSession, message) -> callSession.channel.writeAndFlush(message.toByteBuf())
				);
			}
		});

		for (int i = 0; i < MESSAGES; i++) {
			streamClient.channel.writeAndFlush(
				new Message(
						UUID.randomUUID(),
						MessageTypeEnum.DATA,
						"Not sessioned message from client #" + i
				).toByteBuf()
			);
		}
	}
}