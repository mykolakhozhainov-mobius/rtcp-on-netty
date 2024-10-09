package edu.netty.server.processor;

import edu.netty.common.ServerChannelUtils;
import edu.netty.server.channel.MessageChannel;
import edu.netty.server.channel.transports.StreamMessageChannel;
import edu.netty.server.channel.transports.StreamChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StreamMessageProcessor extends MessageProcessor {
     public final Map<String, StreamMessageChannel> messageChannels;
     private Channel channel;

     public EventLoopGroup bossGroup;
     public EventLoopGroup workerGroup;

     public StreamMessageProcessor(int port) {
          messageChannels = new ConcurrentHashMap<>();
          bossGroup = ServerChannelUtils.createEventLoopGroup();
          workerGroup = ServerChannelUtils.createEventLoopGroup();

          this.port = port;
     }

     @Override
     public MessageChannel createMessageChannel(Channel channel) {
          // IP + Port
          InetSocketAddress socketAddress = ((InetSocketAddress) channel.remoteAddress());
          String key = socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();

          StreamMessageChannel retval = messageChannels.get(key);

          if (retval == null) {
               System.out.println("[TCP-PROCESSOR] Channel with key " + key + " (" + channel.id() + ") created");
               retval = new StreamMessageChannel(this, channel);
               this.messageChannels.put(key, retval);
          }

          System.out.println("[TCP-PROCESSOR] Channel with key " + key + " (" + channel.id() + ") used for processing message");

          return retval;
     }

     @Override
     public void start() {
          try {
               ServerBootstrap bootstrap = new ServerBootstrap();

               bootstrap.group(bossGroup, workerGroup)
                       .handler(new LoggingHandler(LogLevel.DEBUG));

               // Configuration for TCP connection
               bootstrap.channel(ServerChannelUtils.getSocketChannel())
                    .childHandler(new StreamChannelInitializer(this))
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
               System.out.println("[TCP-PROCESSOR] Server started on port " + port);
          } catch (InterruptedException e) {
               System.out.println(e);
          }
     }

     public void remove(StreamMessageChannel streamMessageChannel) {
          String key = streamMessageChannel.getKey();

          if (messageChannels.get(key) == streamMessageChannel) {
               this.messageChannels.remove(key);
          }
     }
}
