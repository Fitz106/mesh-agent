package com.alibaba.dubbo.performance.demo.agent.consumer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.flush.FlushConsolidationHandler;
public class HttpConsumerServerInitializer extends ChannelInitializer<SocketChannel> {
    //static final EventExecutorGroup group = new DefaultEventExecutorGroup(1);
    public HttpConsumerServerInitializer() {
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        //p.addLast("logging", new LoggingHandler(LogLevel.INFO));
        //p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));
        //p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());
        
        //p.addLast(new FlushConsolidationHandler(1000,false));
        
        p.addLast("codec",new HttpServerCodec());
        p.addLast("aggregator",new HttpObjectAggregator(512*1024));
        //p.addLast(group,new HttpConsumerServerHandler());
        //p.addLast(new DispatcherHandler());
        p.addLast(new NewDispatcherHandler());
    }
}