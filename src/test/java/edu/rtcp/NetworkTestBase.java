package edu.rtcp;

import com.mobius.software.common.dal.timers.WorkerPool;

import edu.rtcp.common.TransportEnum;
import edu.rtcp.common.message.Message;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicLong;

public class NetworkTestBase {
	protected RtcpStack serverStack;
	protected RtcpStack localStack;
	private WorkerPool workerPool;

	final AtomicLong answerReceived = new AtomicLong(0L);
	final AtomicLong requestReceived = new AtomicLong(0L);

	protected Long idleTimeout = 1000L;
	protected Long responseTimeout = 1000L;
//    protected Long reconnectTimeout = 1000L;

	protected static final String localLinkID = "1";

	public void setupRemote() throws Exception {
		Configurator.initialize(new DefaultConfiguration());

		if (workerPool == null) {
			workerPool = new WorkerPool();
			workerPool.start(4);
		}
		serverStack = new RtcpStack(4, true, TransportEnum.UDP);
		Provider serverProvider = new Provider(serverStack) {
			@Override
			public void onMessage(Message message, AsyncCallback callback) {
				requestReceived.incrementAndGet();
			}
		};
		serverStack.registerProvider(serverProvider);
		serverStack.getNetworkManager().addLink(localLinkID, InetAddress.getByName("127.0.0.1"), 8081,
				InetAddress.getByName("127.0.0.1"), 8080);
		serverStack.getNetworkManager().startLink(localLinkID);
	}

	public void setupLocal() throws Exception {
		if (workerPool == null) {
			workerPool = new WorkerPool();
			workerPool.start(4);
		}

		localStack = new RtcpStack(4, false, TransportEnum.UDP);
		Provider localProvider = new Provider(localStack) {
			@Override
			public void onMessage(Message message, AsyncCallback callback) {
				answerReceived.incrementAndGet();
			}
		};
		;
		localStack.registerProvider(localProvider);
		localStack.getNetworkManager().addLink(localLinkID, InetAddress.getByName("127.0.0.1"), 8080,
				InetAddress.getByName("127.0.0.1"), 8081);
		localStack.getNetworkManager().startLink(localLinkID);
	}

	public void stopRemote() {
		if (serverStack != null) {
			serverStack.stop();
			serverStack = null;
		}
	}

	public void stopLocal() {
		if (localStack != null) {
			localStack.stop();
			localStack = null;
		}
	}

}
