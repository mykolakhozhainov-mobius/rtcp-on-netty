package edu.netty.client.handlers;

import java.util.Map;
import java.util.UUID;

import edu.netty.common.message.Message;
import edu.netty.common.session.Session;
import edu.netty.common.session.SessionStateEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	public final Map<UUID, Session> sessions;

	public ClientHandler(Map<UUID, Session> sessions) {
		this.sessions = sessions;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[CLIENT-HANDLER] Channel activated");
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);

		Message message = (Message) msg;
		System.out.println(message);

		UUID sessionId = message.sessionId;

		Session session = sessions.get(sessionId);

		if (session == null) return;

		if (session.state == SessionStateEnum.REQUEST) {
			session.state = SessionStateEnum.LISTEN;

			System.out.print("[ACK-PROCESSING]");
			session.addMessageTask(message, (callSession, callMessage) -> {
				session.state = SessionStateEnum.LISTEN;
				System.out.println("[CLIENT-HANDLER] ACK from session [" + message.sessionId + "] processed");
				session.runTask();
			});

			session.runTask();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("exceptionCaught");
		super.exceptionCaught(ctx, cause);

		cause.printStackTrace();
		ctx.close();

	}

}
