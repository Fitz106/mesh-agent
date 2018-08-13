package com.alibaba.dubbo.performance.demo.agent.dubbo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.flush.FlushConsolidationHandler;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    //static final EventExecutorGroup group = new DefaultEventExecutorGroup(4);
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new DubboRpcHandler());
		pipeline.addLast(new LengthFieldBasedFrameDecoder(65536,12,4));
        pipeline.addLast(new NewRpcClientHandler());
    }
}
