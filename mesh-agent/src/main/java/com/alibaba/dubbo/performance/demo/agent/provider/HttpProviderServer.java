package com.alibaba.dubbo.performance.demo.agent.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 */
public  class HttpProviderServer {
    public static void start(int PORT) throws Exception {
        // Configure the server.
        //EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
        EventLoopGroup workerGroup = new EpollEventLoopGroup(1);
          //EpollServerSocketChannel.class
        //NioServerSocketChannel.class
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(EpollServerSocketChannel.class)
             .childOption(ChannelOption.SO_KEEPALIVE,true)
			 .childOption(ChannelOption.TCP_NODELAY, true)
             .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
             .childHandler(new HttpProviderServerInitializer());

            Channel ch = b.bind(PORT).sync().channel();

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
