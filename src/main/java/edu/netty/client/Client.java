package edu.netty.client;

import edu.netty.client.callback.Executable;
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
		this.sessions = new HashMap<UUID, Session>();
	}

	public void start() throws InterruptedException {
		NioEventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) {
							channel = socketChannel;
							socketChannel.pipeline().addLast(new MobiusClientInitializer(sessions));
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

//	public void sendMessage(Message message) throws InterruptedException {
//		for (Session s : sessions) {
//			if (s.id == message.sessionId) {
//				if (s.state == SessionStateEnum.INIT) {
//					
//					s.setState(SessionStateEnum.REQUEST);
//					executor.addTaskLast(new IdentifiedTask() {
//
//						@Override
//						public void execute() {
//
//							channel.writeAndFlush(message.toByteBuf());
//						}
//
//						@Override
//						public long getStartTime() {
//							return System.currentTimeMillis();
//						}
//
//						@Override
//						public String getId() {
//							if (message.sessionId != null) {
//
//								return message.sessionId.toString();
//							}
//							return String.valueOf(System.currentTimeMillis());
//						}
//					});
//				}
//				else {
//					System.out.println("Trying "+ s.state);
//					Thread.sleep(1000);
//					sendMessage(message);
//				}
//
//			}
//		}
//
//	}

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
		client.executor.start(2, 1000);
//		client.executor.wait();

		client.createSession(UUID.randomUUID());
		client.createSession(UUID.randomUUID());
//		client.createSession(UUID.randomUUID());
//		client.createSession(UUID.randomUUID());
//		client.createSession(UUID.randomUUID());
//		client.createSession(UUID.randomUUID());
//		client.createSession(UUID.randomUUID());
//		client.createSession(UUID.randomUUID());
//		client.createSession(UUID.randomUUID());
//		client.createSession(UUID.randomUUID());
		
		client.sessions.values().forEach(session -> {
			session.addMessageTask(
					new Message(session.id, MessageTypeEnum.OPEN, "1"),
                    (callSession, message) -> callSession.channel.writeAndFlush(message.toByteBuf())
            );

			for (int i = 0; i < 10; i++) {
				session.addMessageTask(
					new Message(session.id, MessageTypeEnum.DATA, String.valueOf(i)),
					(callSession, message) -> callSession.channel.writeAndFlush(message.toByteBuf())
				);
			}
		});

//		client.sendMessage(new Message("Not from session"));
	}
}