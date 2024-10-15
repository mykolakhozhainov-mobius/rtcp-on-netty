package edu.rtcp.server.session;

import java.net.InetSocketAddress;
import java.util.UUID;

import edu.rtcp.RtcpStack;
import edu.rtcp.server.executor.MessageExecutor;
import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.executor.tasks.MessageTask;
import edu.rtcp.server.provider.Provider;

public abstract class Session {
	protected UUID id;
	protected SessionStateEnum state;
	protected Provider provider;
	public InetSocketAddress remoteAddress;

	// private MessageFactory messageFactory;

	public UUID getId() {
		return this.id;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public void setSessionState(SessionStateEnum state) {
		this.state = state;
	}

	// Handles default messages
	public void processRequest(Message request, AsyncCallback callback) {
		// TODO: define message types
		// Idea: we have sessionId field in every message
		// We may organize as if sessionId is defined:
		// - session is not exist or IDLE -> create session
		// - session exists -> data
		// - session exists and message type is Bye -> close session

//		if (request instanceof OpenSession) {
			if (this.state != SessionStateEnum.IDLE) {
				callback.onError(new RuntimeException("Session has already been opened"));
				return;
			}

			this.state = SessionStateEnum.OPEN;
			this.provider.getSessionStorage().store(this);
			this.provider.getListener().onInitialRequest(request, this, callback);
//		} else if (request instanceof CloseSession) {
//			if (this.state == SessionStateEnum.CLOSED) {
//				callback.onError(new RuntimeException("Session has already been closed"));
//				return;
//			}
//
//			this.state = SessionStateEnum.CLOSED;
//			this.provider.getSessionStorage().remove(this);
//			this.provider.getListener().onTerminationRequest(request, this, callback);
//		} else if (request instanceof DataPacket) {
//			if (this.state == SessionStateEnum.IDLE || this.state == SessionStateEnum.CLOSED) {
//				callback.onError(new RuntimeException("Session can not handle data requests in this moment"));
//				return;
//			}
//
//			this.provider.getListener().onDataRequest(request, this, callback);
//		} else {
//			callback.onError(new RuntimeException("Unknown request type"));
//			return;
//		}
//
//		this.lastRequest = request;
	}

	// Handles acks
	public void processResponse(Message message, AsyncCallback callback) {


		//		if (this.lastRequest == null) {
//			callback.onError(new RuntimeException("Session received response with no request been sent"));
//			return;
//		}
//
//		// TODO: Check type of the message according to type of last request
//		this.provider.getListener().onAcknowledgementResponse(message, this, callback);
	}
	
	public void sendAnswer(Message message, int port, AsyncCallback callback) {
		RtcpStack stack = this.provider.getStack();
		MessageExecutor executor = stack.getMessageExecutor();
		
		executor.addTaskLast(new MessageTask() {
			@Override
			public void execute() {
				stack.getNetworkManager().sendMessage(message, port, callback);
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
		});
	}
}