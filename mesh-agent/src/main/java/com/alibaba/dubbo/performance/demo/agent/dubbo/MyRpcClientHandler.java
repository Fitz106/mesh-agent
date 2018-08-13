package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcRequestAndSessionHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Arrays;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@ChannelHandler.Sharable
public class MyRpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws InterruptedException {
        String requestId = response.getRequestId();
        Channel channel= RpcRequestAndSessionHolder.get(requestId);
        if(null != channel){
            byte[] result = response.getBytes();
            Integer value = JSON.parseObject(result, Integer.class);
            String str = requestId + "&"+value;//方法二特有的
            FullHttpResponse agentResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(str,CharsetUtil.UTF_8));
            agentResponse.headers().set("Content-Type", "text/plain");
            agentResponse.headers().setInt("Content-Length", agentResponse.content().readableBytes());
            agentResponse.headers().set("Connection", "keep-alive");

            channel.writeAndFlush(agentResponse);
            RpcRequestAndSessionHolder.remove(requestId);
        }
    }


}
