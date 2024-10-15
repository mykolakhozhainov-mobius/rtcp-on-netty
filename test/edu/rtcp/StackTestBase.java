package edu.rtcp;

import edu.rtcp.server.network.executor.MessageExecutor;
import edu.rtcp.server.network.processor.ProcessorFactory;
import edu.rtcp.server.network.processor.transport.StreamProcessor;
import edu.rtcp.server.provider.Provider;

public class StackTestBase {
    protected ProcessorFactory processorFactory = new ProcessorFactory();

    protected final int SERVER_PORT = 8080;
    protected final int CLIENT_PORT = 8081;

    public RtcpStack serverStack = new RtcpStack();
    public RtcpStack localStack = new RtcpStack();

    protected void setupServer() {
        StreamProcessor processor = (StreamProcessor) processorFactory.getStreamProcessor(SERVER_PORT, this.serverStack);
        processor.setExecutor(new MessageExecutor());

        processor.run(4, 1000);

        this.serverStack.setProcessor(processor);

        Provider provider = new Provider(this.serverStack);
        this.serverStack.setProvider(provider);

        //serverStack.connect(); to local stack
    }

    protected void setupLocal() {
        StreamProcessor processor = (StreamProcessor) processorFactory.getStreamProcessor(CLIENT_PORT, this.localStack);
        processor.setExecutor(new MessageExecutor());

        processor.run(4, 1000);

        this.localStack.setProcessor(processor);

        Provider provider = new Provider(this.localStack);
        this.localStack.setProvider(provider);

        //localStack.connect(); to server stack
    }
}
