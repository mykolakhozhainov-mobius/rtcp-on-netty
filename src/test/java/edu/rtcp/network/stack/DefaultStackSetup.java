package edu.rtcp.network.stack;

import edu.rtcp.RtcpStack;
import edu.rtcp.common.TransportEnum;
import edu.rtcp.server.provider.Provider;

import java.net.InetAddress;
import java.util.Random;

public class DefaultStackSetup implements StackSetup {
	private RtcpStack serverStack;
	private RtcpStack clientStack;

	private final int SERVER_PORT;
	private final int CLIENT_PORT;

	protected static final String localLinkID = "1";
	private final TransportEnum transport;
	private final int threadPoolSize;

	public DefaultStackSetup(TransportEnum transport, int threadPoolSize) {
		this.transport = transport;
		this.threadPoolSize = threadPoolSize;

		this.SERVER_PORT = new Random().nextInt(20000) + 1024;
		this.CLIENT_PORT = new Random().nextInt(20000) + 1024;
	}

	@Override
	public RtcpStack setupServer() throws Exception {
		this.serverStack = new RtcpStack(
				this.threadPoolSize,
				true,
				this.transport,
				false
		);

		this.serverStack.registerProvider(new Provider(serverStack));

		this.serverStack.getNetworkManager()
				.addLink(
						localLinkID,
						InetAddress.getByName("127.0.0.1"),
						this.CLIENT_PORT,
						InetAddress.getByName("127.0.0.1"),
						this.SERVER_PORT);

		this.serverStack.getNetworkManager().startLink(localLinkID);

		return this.serverStack;
	}

	@Override
	public RtcpStack setupClient() throws Exception {
		this.clientStack = new RtcpStack(
				this.threadPoolSize,
				false,
				this.transport,
				false);

		Provider localProvider = new Provider(this.clientStack);

		this.clientStack.registerProvider(localProvider);
		this.clientStack.getNetworkManager()
				.addLink(
						localLinkID,
						InetAddress.getByName("127.0.0.1"),
						this.SERVER_PORT,
						InetAddress.getByName("127.0.0.1"),
						this.CLIENT_PORT
				);

		this.clientStack.getNetworkManager().startLink(localLinkID);

		return this.clientStack;
	}

	@Override
	public RtcpStack getServerStack() {
		return this.serverStack;
	}

	@Override
	public RtcpStack getClientStack() {
		return this.clientStack;
	}

	@Override
	public int getServerPort() {
		return this.SERVER_PORT;
	}

	@Override
	public int getClientPort() {
		return this.CLIENT_PORT;
	}
}
