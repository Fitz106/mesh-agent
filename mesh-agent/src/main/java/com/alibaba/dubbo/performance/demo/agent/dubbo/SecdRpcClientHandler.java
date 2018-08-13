package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.provider.ChannelCountMapUtil;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.provider.RpcChannelUtil;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
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

import java.util.Arrays;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@ChannelHandler.Sharable
public class SecdRpcClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws InterruptedException {
        if(byteBuf.getByte(2) == 0x06){
            long requestId = byteBuf.getLong(4);
            Channel channel = RpcRequestAndSessionHolder.get(String.valueOf(requestId));
            if (null != channel) {
                byteBuf.retain();
                channel.writeAndFlush(byteBuf,channel.voidPromise());
                RpcRequestAndSessionHolder.remove(String.valueOf(requestId));
            } 
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
