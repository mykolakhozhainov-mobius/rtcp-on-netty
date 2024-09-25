package edu.netty.server;

import edu.netty.common.executor.MessageProcessorExecutor;
import edu.netty.common.executor.ProcessorExecutor;
import edu.netty.server.channel.MessageChannel;
import edu.netty.server.channel.MessageChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageProcessor {
     public final Map<String, MessageChannel> messageChannels;
     //public final Map<Integer, Session> sessions;
     private final int port;
     private Channel channel;

     public EventLoopGroup bossGroup;
     public EventLoopGroup workerGroup;

     public ProcessorExecutor executor;

     public MessageProcessor(int port) {
          messageChannels = new ConcurrentHashMap<String, MessageChannel>();
          bossGroup = new EpollEventLoopGroup();
          workerGroup = new EpollEventLoopGroup();

          executor = new MessageProcessorExecutor();

          this.port = port;
     }

     public Class<? extends ServerSocketChannel> getEpollServerSocketChannel() {
          return EpollServerSocketChannel.class;
     }

     private MessageChannel createMessageChannel(String key, InetAddress targetHost, int port) {
          MessageChannel retval = messageChannels.get(key);

          if (retval == null) {
               retval = new MessageChannel(targetHost, port, this);
               this.messageChannels.put(key, retval);
          }
          return retval;
     }

     public MessageChannel createMessageChannel(InetAddress targetHost, int port) throws IOException {
          String key = MessageChannel.getKey(targetHost, port);
          MessageChannel retval = messageChannels.get(key);

          if (retval == null) {
               retval = createMessageChannel(key, targetHost, port);
          }
          return retval;
     }

     public MessageChannel createMessageChannel(Channel channel) {
          // IP + Port
          InetSocketAddress socketAddress = ((InetSocketAddress) channel.remoteAddress());
          String key = socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();

          System.out.println("[PROCESSOR] Channel with key " + key + " (" + channel.id() + ") created");

          MessageChannel retval = messageChannels.get(key);

          if (retval == null) {
               retval = new MessageChannel(this, channel);
               this.messageChannels.put(key, retval);
          }

          return retval;
     }

     public void start() {
          try {
               ServerBootstrap bootstrap = new ServerBootstrap();

               bootstrap.group(bossGroup, workerGroup)
                       .handler(new LoggingHandler(LogLevel.DEBUG));

               // Configuration for TCP connection
               bootstrap.channel(this.getEpollServerSocketChannel())
                    .childHandler(new MessageChannelInitializer(this))
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

               // Bind and start to accept incoming connections.
               channel = bootstrap.bind(port).await().channel();

               // Create a new thread to run the server
               new Thread(() -> {
                    try {
                         channel.closeFuture().sync();
                    } catch (InterruptedException e) {
                         System.out.println(e);
                    } finally {
                         workerGroup.shutdownGracefully();
                         bossGroup.shutdownGracefully();
                    }
               }).start();
               System.out.println("[PROCESSOR] Server started on port " + port);
          } catch (InterruptedException e) {
               System.out.println(e);
          }
     }

     public void remove(MessageChannel messageChannel) {
          String key = messageChannel.getKey();

          if (messageChannels.get(key) == messageChannel) {
               this.messageChannels.remove(key);
          }
     }

     public static void main(String[] args) {
          MessageProcessor processor = new MessageProcessor(8080);
          processor.start();

          processor.executor.start(8, 1000);
     }
}
