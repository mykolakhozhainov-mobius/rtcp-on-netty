package edu.rtcp;

import com.mobius.software.common.dal.timers.WorkerPool;

import edu.rtcp.common.TransportEnum;
//import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
//import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.provider.Provider;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import java.net.InetAddress;
import java.util.ArrayList;

public class NetworkTestBase {
	protected RtcpStack serverStack;
	protected RtcpStack localStack;
	private WorkerPool workerPool;
    private static final TransportEnum TRANSPORT = TransportEnum.TCP;
    private static final boolean LOGGING = true;

	protected Long idleTimeout = 1000L;
	protected Long responseTimeout = 1000L;
//    protected Long reconnectTimeout = 1000L;

	protected static final ArrayList<String> localLinkIDs = new ArrayList();

	public void setupRemote() throws Exception {
		Configurator.initialize(new DefaultConfiguration());

		if (workerPool == null) {
			workerPool = new WorkerPool();
			workerPool.start(4);
		}
		serverStack = new RtcpStack(8, true, TRANSPORT, LOGGING);
		Provider serverProvider = new Provider(serverStack);
		serverStack.registerProvider(serverProvider);
		serverStack.getNetworkManager().addLink("1", InetAddress.getByName("127.0.0.1"), 8081,
				InetAddress.getByName("127.0.0.1"), 8080);
		serverStack.getNetworkManager().addLink("2", InetAddress.getByName("127.0.0.1"), 8083,
				InetAddress.getByName("127.0.0.1"), 8080);
		serverStack.getNetworkManager().addLink("3", InetAddress.getByName("127.0.0.1"), 8085,
				InetAddress.getByName("127.0.0.1"), 8080);
		serverStack.getNetworkManager().addLink("4", InetAddress.getByName("127.0.0.1"), 8087,
				InetAddress.getByName("127.0.0.1"), 8080);
//		serverStack.getNetworkManager().startLink(localLinkID);
	}

	public void setupLocal() throws Exception {
		if (workerPool == null) {
			workerPool = new WorkerPool();
			workerPool.start(4);
		}

		localStack = new RtcpStack(8, false, TRANSPORT, LOGGING);
		Provider localProvider = new Provider(localStack);
		;
		localStack.registerProvider(localProvider);
		localStack.getNetworkManager().addLink("1", InetAddress.getByName("127.0.0.1"), 8080,
				InetAddress.getByName("127.0.0.1"), 8081);
		localStack.getNetworkManager().addLink("2", InetAddress.getByName("127.0.0.1"), 8080,
				InetAddress.getByName("127.0.0.1"), 8083);
		localStack.getNetworkManager().addLink("3", InetAddress.getByName("127.0.0.1"), 8080,
				InetAddress.getByName("127.0.0.1"), 8085);
		localStack.getNetworkManager().addLink("4", InetAddress.getByName("127.0.0.1"), 8080,
				InetAddress.getByName("127.0.0.1"), 8087);
//		localStack.getNetworkManager().startLink(localLinkID);
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
