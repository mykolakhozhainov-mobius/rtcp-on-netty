package edu.rtcp.server.session;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.executor.tasks.MessageTask;
import edu.rtcp.server.provider.Provider;

public abstract class Session {
	protected int id;
	protected SessionStateEnum state;
	protected Provider provider;

	public int getId() {
		return this.id;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public SessionStateEnum getSessionState() {
		return this.state;
	}

	public void setSessionState(SessionStateEnum state) {
		this.state = state;
	}

	public abstract void processRequest(RtcpBasePacket request, boolean isNewSession, AsyncCallback callback);
	public abstract void processAnswer(RtcpBasePacket answer, AsyncCallback callback);

	public abstract boolean isServer();

	public void sendMessage(RtcpBasePacket packet, int port, AsyncCallback callback) {
		MessageTask task = new MessageTask(packet) {
			@Override
			public void execute() {
				provider.getStack().getNetworkManager().sendMessage(packet, port, callback);
			}
		};

		this.sendMessageAsTask(task);
	}

	public void sendMessageAsTask(MessageTask task) {
		this.provider.getStack().getMessageExecutor().addTaskLast(task);
	}
}