package edu.rtcp;

//import static org.junit.Assert.assertEquals;
import edu.rtcp.common.message.Message;
import edu.rtcp.common.message.MessageTypeEnum;
import edu.rtcp.common.message.rtcp.header.RtcpBasePacket;
import edu.rtcp.server.callback.AsyncCallback;
import edu.rtcp.server.network.NetworkLink;
import edu.rtcp.server.session.Session;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.UUID;


public class PerformanceTest extends NetworkTestBase {
    protected static final String localListenerID = "1";
    static final int numberOfSessions = 10;
    static final int numberOfPacketsPerSession = 5;
    public ArrayList<Session> sessions= new ArrayList<>();
    
    @Test
    public void testEvent() throws Exception
    {
        super.setupRemote();
        super.setupLocal();

        RtcpStack localStack = this.localStack;
        RtcpStack serverStack = this.serverStack;

        System.out.println(localStack);
        System.out.println(serverStack);

        NetworkLink localLink = localStack.getNetworkManager().getLinkByLinkId(localLinkID);
        
        System.out.println("LINK : " + localLink);
        
        for(int i = 0; i < numberOfSessions; i++) {
            UUID sessionId = UUID.randomUUID();
//            Message msg = new Message(sessionId,MessageTypeEnum.OPEN ,"Open session");
//            RtcpBasePacket msg = new RtcpBasePacket;
            sessions.add(localStack.getProvider().getSessionFactory().createClientSession(msg));
            sessions.get(i).sendMessage(msg, localLink.getRemotePort(), new AsyncCallback() {
            	public void onSuccess()
              {
                  System.out.println("onSuccess");
              }

              public void onError(Exception e) {
                  System.out.println(e);
              }
            });
        }
        
        try
        {
            Thread.sleep(responseTimeout);
        }
        catch(InterruptedException ex)
        {
        	System.out.println(ex);
        }
        
        try
        {
        	Thread.sleep(idleTimeout * 2);
        }
        catch(InterruptedException ex)
        {
        	System.out.println(ex);
        }
        
        

        super.stopLocal();
        super.stopRemote();
        
		try
		{
			Thread.sleep(responseTimeout);
		}
		catch(InterruptedException ex)
		{
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