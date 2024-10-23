package edu.rtcp.server.network.processor.transport;

import edu.rtcp.common.ServerChannelUtils;
import edu.rtcp.RtcpStack;
import edu.rtcp.server.network.processor.AbstractProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class StreamProcessor extends AbstractProcessor {
     public Channel channel;

     public EventLoopGroup bossGroup;
     public EventLoopGroup workerGroup;

     public StreamProcessor(int port, RtcpStack stack) {
          this.stack = stack;

          this.bossGroup = ServerChannelUtils.createEventLoopGroup();
          this.workerGroup = ServerChannelUtils.createEventLoopGroup();

          this.port = port;
     }

     @Override
     public void start() {
          try {
               ServerBootstrap bootstrap = new ServerBootstrap();

               bootstrap.group(bossGroup, workerGroup)
                       .handler(new LoggingHandler(LogLevel.DEBUG));

               // Configuration for TCP connection
               bootstrap.channel(ServerChannelUtils.getSocketChannel())
                    .childHandler(new StreamChannelInitializer(this.stack))
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
}
