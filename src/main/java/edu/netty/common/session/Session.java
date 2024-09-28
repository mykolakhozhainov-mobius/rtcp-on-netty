package edu.netty.common.session;

import java.util.LinkedList;
import java.util.UUID;

import com.sun.tools.javac.util.List;

import edu.netty.common.executor.MessageProcessorExecutor;
import edu.netty.common.message.Message;
import edu.netty.server.task.IdentifiedTask;
import io.netty.channel.Channel;

public class Session {
	public UUID id;
	public Channel channel;
	public LinkedList<IdentifiedTask> tasks;
	public SessionStateEnum state;
	public MessageProcessorExecutor executor;
	
	public Session (UUID id, Channel channel, MessageProcessorExecutor executor) {
		this.channel = channel;
		this.id = id;
		this.state = state.INIT; 
		this.executor = executor;
		this.tasks = new LinkedList<IdentifiedTask>();
	}
	
	public void runTask() {
		Boolean isPositiveState = state == SessionStateEnum.INIT || state == SessionStateEnum.LISTEN;
		
		if (!tasks.isEmpty() && isPositiveState) {
			executor.addTaskLast(tasks.poll());
		}
	}
	
	public void addTask(IdentifiedTask task) {
		tasks.addLast(task);
		if (state == SessionStateEnum.LISTEN || state == SessionStateEnum.INIT) {
			executor.addTaskLast(tasks.poll());
			state = SessionStateEnum.REQUEST;
		}
	}
	
	public void addMessageTask(Message message) {
		addTask(new IdentifiedTask() {

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
				if (id != null) {

					return id.toString();
				}
				return String.valueOf(System.currentTimeMillis());
			}
		});
	}
}