package edu.rtcp.server.session;

import java.net.InetSocketAddress;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.executor.tasks.MessageOutgoingTask;
import edu.rtcp.server.executor.tasks.MessageTask;
import edu.rtcp.server.provider.Provider;

public abstract class Session {
	protected int id;
	protected SessionStateEnum state;
	protected Provider provider;

	public int getId() {
		return this.id;
	}

	public Provider getProvider() {
		return this.provider;
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

	public abstract void processRequest(RtcpBasePacket request, InetSocketAddress address, boolean isNewSession, AsyncCallback callback);
	public abstract void processAnswer(RtcpBasePacket answer, AsyncCallback callback);

	public abstract boolean isServer();

	public void sendMessage(RtcpBasePacket packet,  InetSocketAddress address, AsyncCallback callback) {
		this.sendMessageAsTask(new MessageOutgoingTask(this, packet, address, callback));
	}

	public void sendMessageAsTask(MessageTask task) {
		this.provider.getStack().getMessageExecutor().addTaskLast(task);
	}
}