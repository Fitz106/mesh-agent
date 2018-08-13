package com.alibaba.dubbo.performance.demo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.consumer.async.NewAgentClientHandler;
import com.alibaba.dubbo.performance.demo.agent.dubbo.DubboRpcDecoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.DubboRpcEncoder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.MyRpcClientHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.buffer.PooledByteBufAllocator;
public class NettyChannelPoolHandler implements ChannelPoolHandler {
    //static final EventExecutorGroup group = new DefaultEventExecutorGroup(1);
    @Override
    public void channelReleased(Channel ch) throws Exception {
        //logger.info("channelReleased. Channel ID: " + ch.id()+ ch.remoteAddress());
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        //logger.info("channelAcquired. Channel ID: " + ch.id()+ ch.remoteAddress());
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        SocketChannel channel = (SocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);
        channel.pipeline()
		.addLast(new LengthFieldBasedFrameDecoder(65536,12,4))
        .addLast(new NewAgentClientHandler());

    }
}
