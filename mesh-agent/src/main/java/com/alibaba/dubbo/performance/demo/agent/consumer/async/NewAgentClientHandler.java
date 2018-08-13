package com.alibaba.dubbo.performance.demo.agent.consumer.async;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcRequestAndSessionHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandler;
import java.util.Arrays;
import java.util.Map;
import com.alibaba.dubbo.performance.demo.agent.consumer.EventLoopUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@ChannelHandler.Sharable
public class NewAgentClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    //private Logger logger = LoggerFactory.getLogger(NewAgentClientHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        long requestId = byteBuf.getLong(4);
        AgentRequestAndSessionHolder agentRequestAndSessionHolder = EventLoopUtil.getMap2().get(channelHandlerContext.channel().eventLoop());
        Channel channel= agentRequestAndSessionHolder.get(String.valueOf(requestId));
        if(null != channel) {
            byte[] result = new byte[byteBuf.getInt(12)-1];
            byteBuf.getBytes(17,result);
            
            try{
                Integer value = JSON.parseObject(result, Integer.class);
                
                FullHttpResponse agentResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(String.valueOf(value), CharsetUtil.UTF_8));
                agentResponse.headers().set("Content-Type", "text/plain");
                agentResponse.headers().setInt("Content-Length", agentResponse.content().readableBytes());
                agentResponse.headers().set("Connection", "keep-alive");
                
                channel.writeAndFlush(agentResponse,channel.voidPromise());
                
                agentRequestAndSessionHolder.remove(String.valueOf(requestId));
            }catch (Exception e){
                //logger.info(e.getMessage());
            }
        }
    }
}
