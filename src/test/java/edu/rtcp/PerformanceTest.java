package edu.rtcp;

//import static org.junit.Assert.assertEquals;
import edu.rtcp.common.message.rtcp.factory.PacketFactory;
import edu.rtcp.common.message.rtcp.packet.SenderReport;
import edu.rtcp.common.message.rtcp.types.PacketTypeEnum;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.NetworkLink;
import edu.rtcp.server.session.Session;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

public class PerformanceTest extends NetworkTestBase {
    protected static final String localListenerID = "1";

    static final int numberOfSessions = 10;
    static final int numberOfPacketsPerSession = 5;

    private final PacketFactory packetFactory = new PacketFactory();
    public ArrayList<Session> sessions = new ArrayList<>();
    
    @Test
    public void testEvent() throws Exception {
        super.setupRemote();
        super.setupLocal();

        RtcpStack localStack = this.localStack;
        RtcpStack serverStack = this.serverStack;

        System.out.println(localStack);
        System.out.println(serverStack);

        NetworkLink localLink = localStack.getNetworkManager().getLinkByLinkId(localLinkID);
        
        System.out.println("LINK : " + localLink);
        
        for (int i = 0; i < numberOfSessions; i++) {
            SenderReport packet = this.packetFactory.createSenderReport(
                    (byte) 1,
                    0,
                    null,
                    null
            );

            sessions.add(localStack.getProvider()
                    .getSessionFactory()
                    .createClientSession(packet)
            );

            sessions.get(i).sendMessage(
                    packet,
                    localLink.getRemotePort(),
                    new AsyncCallback() {
                        public void onSuccess()
              {
                  System.out.println("onSuccess");
              }

                        public void onError(Exception e) {
                  System.out.println(e);
              }
            });
        }
        
        try {
            Thread.sleep(responseTimeout);
        }
        catch(InterruptedException ex) {
        	System.out.println(ex);
        }
        
        try {
        	Thread.sleep(idleTimeout * 2);
        }
        catch(InterruptedException ex) {
        	System.out.println(ex);
        }

        super.stopLocal();
        super.stopRemote();
        
		try {
			Thread.sleep(responseTimeout);
		}
		catch(InterruptedException ex) {
        	System.out.println(ex);
		}
		
		assertEquals(requestReceived.get() , 10L);
//		assertEquals(ccaReceivedByListener.get() , 1L);
//		assertEquals(ccrReceived.get() , 1L);
//		assertEquals(ccrReceivedByListener.get() , 1L);
//		assertEquals(timeoutReceived.get() , 0L);
//        
        System.out.println("REQUESTS: " + requestReceived.get());
    }
}