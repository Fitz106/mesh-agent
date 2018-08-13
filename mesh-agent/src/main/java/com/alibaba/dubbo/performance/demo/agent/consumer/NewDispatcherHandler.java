package com.alibaba.dubbo.performance.demo.agent.consumer;

import com.alibaba.dubbo.performance.demo.agent.consumer.async.*;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClientInitializer;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;

import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
@ChannelHandler.Sharable
public class NewDispatcherHandler extends SimpleChannelInboundHandler {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            ByteBuf content = request.content();
            AgentRequest agentRequest = new AgentRequest();
            byte[] header = new byte[12];
            int len = content.readableBytes()+8;
            Bytes.int2bytes(len,header,0);
            Bytes.long2bytes(agentRequest.getId(),header,4);

            content.retain();

            CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
            compositeByteBuf.addComponents(  true  ,  Unpooled.wrappedBuffer(header) , content);
            String str = content.toString(io.netty.util.CharsetUtil.UTF_8);
            String interfaceName = str.split("&")[0].split("=")[1];



            List<Endpoint> endpoints  = EtcdUtil.getEndpoints(interfaceName);
             int num = (int)(agentRequest.getId()%EtcdUtil.getEndpoints(interfaceName).size());
            Endpoint endpoint = EtcdUtil.getEndpoints(interfaceName).get(num);


            if( !EventLoopUtil.getMap().containsKey(ctx.channel().eventLoop())){
                NettyPoolClient nettyPoolClient = new NettyPoolClient();
                nettyPoolClient.build(ctx.channel().eventLoop());
                for(int i = 0;i<endpoints.size();i++){
                    nettyPoolClient.poolMap.get(endpoints.get(i).getAgentAddr());
                }
                EventLoopUtil.getMap().put(ctx.channel().eventLoop(),nettyPoolClient);
                AgentRequestAndSessionHolder agentRequestAndSessionHolder = new AgentRequestAndSessionHolder();
                EventLoopUtil.getMap2().put(ctx.channel().eventLoop(),agentRequestAndSessionHolder);
            }
            if( !EventLoopUtil.getMap2().containsKey(ctx.channel().eventLoop())){
                AgentRequestAndSessionHolder agentRequestAndSessionHolder = new AgentRequestAndSessionHolder();
                EventLoopUtil.getMap2().put(ctx.channel().eventLoop(),agentRequestAndSessionHolder);
            }
            EventLoopUtil.getMap2().get(ctx.channel().eventLoop()).put(String.valueOf(agentRequest.getId()),ctx.channel());
            SimpleChannelPool pool = EventLoopUtil.getMap().get(ctx.channel().eventLoop()).poolMap.get(endpoint.getAgentAddr());
            Future<Channel> f = pool.acquire();
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    Channel ch = f1.getNow();
                    ch.writeAndFlush(compositeByteBuf,ch.voidPromise());
                    pool.release(ch);
                }
            });
        }
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
