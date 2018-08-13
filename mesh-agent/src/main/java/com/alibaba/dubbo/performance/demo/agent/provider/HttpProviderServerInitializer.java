package com.alibaba.dubbo.performance.demo.agent.provider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.flush.FlushConsolidationHandler;

public class HttpProviderServerInitializer extends ChannelInitializer<SocketChannel> {
    //static final EventExecutorGroup group = new DefaultEventExecutorGroup(1);
    public HttpProviderServerInitializer() {
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
        //p.addLast("codec",new HttpServerCodec());
        //p.addLast("aggregator",new HttpObjectAggregator(512*1024));
        //p.addLast(new FlushConsolidationHandler(20,true));
        p.addLast(new LengthFieldBasedFrameDecoder(65536,0,4));
        //p.addLast(new NewProviderServerHandler());
		p.addLast(new SecodProviderServerHandler());
		//p.addLast(new LimitFlushOutAgentHandler());
    }
}
