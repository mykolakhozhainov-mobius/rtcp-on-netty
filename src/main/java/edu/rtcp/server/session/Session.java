package edu.rtcp.server.session;

import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.executor.tasks.MessageOutgoingTask;
import edu.rtcp.server.executor.tasks.MessageTask;
import edu.rtcp.server.network.PendingStorage;
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
		PendingStorage pendingStorage = this.provider.getStack().getNetworkManager().getPendingStorage();

		MessageTask task = new MessageOutgoingTask(packet, port, this.provider.getStack(), callback);

		if (this.state == SessionStateEnum.WAITING) {
			pendingStorage.addTask(packet.getSSRC(), task);
		} else {
			this.provider.getStack().getMessageExecutor().addTaskLast(task);
		}
	}
}