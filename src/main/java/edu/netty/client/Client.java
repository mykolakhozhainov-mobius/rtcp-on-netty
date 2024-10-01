package edu.netty.client;

import edu.netty.common.message.Message;
import edu.netty.common.message.MessageTypeEnum;
import edu.netty.common.session.Session;
import edu.netty.server.task.IdentifiedTask;

import java.util.HashMap;
import java.util.Map;
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
	private static final String host = "localhost";
	private static final int port = 8080;
	private Channel channel;
	public MessageProcessorExecutor executor;
	public final Map<UUID, Session> sessions;

	public Client() {
		this.executor = new MessageProcessorExecutor();
		this.sessions = new HashMap<>();
	}

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
	
	public IdentifiedTask makeMessageTask(Message message) {
		return new IdentifiedTask() {

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
				if (message.sessionId != null) {

					return message.sessionId.toString();
				}
				return String.valueOf(System.currentTimeMillis());
			}
		};
	}

	public void createSession(UUID id) {
		if (this.isSessioned(id)) return;

		Session session = new Session(id, channel, executor);
		this.sessions.put(session.id, session);

		System.out.println("[PROCESSOR] UUID " + id + " added as sessioned");
	}

	public boolean isSessioned(UUID id) {
		return this.sessions.containsKey(id);
	}

	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.start();
		client.executor.start(8, 10);

		final int SESSIONS = 1;
		final int MESSAGES = 1;

		for (int i = 0; i < SESSIONS; i++) { client.createSession(UUID.randomUUID()); }

		client.sessions.values().forEach(session -> {
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
	}
}