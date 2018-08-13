package com.alibaba.dubbo.performance.demo.agent.provider;


import com.alibaba.dubbo.performance.demo.agent.dubbo.DubboRpcEncoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.DubboRpcHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.SecdRpcClientHandler;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;


public class RpcNettyChannelPoolHandler implements ChannelPoolHandler {
    //static final EventExecutorGroup group = new DefaultEventExecutorGroup(1);
    @Override
    public void channelReleased(Channel ch) throws Exception {
//        logger.info("channelReleased. Channel ID: " + ch.id()+ ch.remoteAddress());
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
//        logger.info("channelAcquired. Channel ID: " + ch.id()+ ch.remoteAddress());
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        SocketChannel channel = (SocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);
        channel.pipeline()
        .addLast(new DubboRpcHandler())
        .addLast(new LengthFieldBasedFrameDecoder(65536,12,4))
        .addLast(new SecdRpcClientHandler());
    }
}
