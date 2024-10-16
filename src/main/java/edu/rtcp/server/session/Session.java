package edu.rtcp.server.session;

import java.util.UUID;

import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;

public abstract class Session {
	protected UUID id;
	protected SessionStateEnum state;
	protected Provider provider;

	public UUID getId() {
		return this.id;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public void setSessionState(SessionStateEnum state) {
		this.state = state;
	}

	public abstract void processRequest(Message request, AsyncCallback callback);
	public abstract void processAnswer(Message answer, AsyncCallback callback);

	public abstract boolean isServer();
}