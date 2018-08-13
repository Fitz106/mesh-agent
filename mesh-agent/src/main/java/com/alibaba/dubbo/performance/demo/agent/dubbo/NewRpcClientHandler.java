package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.dubbo.performance.demo.agent.provider.RpcChannelUtil;
import java.util.Arrays;
import java.util.Map;
import io.netty.channel.ChannelHandler;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.channel.ChannelHandler;
import com.alibaba.dubbo.performance.demo.agent.provider.CountRequestUtil;

@ChannelHandler.Sharable
public class NewRpcClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws InterruptedException {
		if(byteBuf.getByte(2) == 0x06){
			byteBuf.retain();
			Channel ch = RpcChannelUtil.getMap().get(channelHandlerContext.channel());
			ch.writeAndFlush(byteBuf,ch.voidPromise());
		}
		/*
		long size = CountRequestUtil.rem();
        if(size <150 || !ch.config().isAutoRead()){
           // logger.info("canread"+size);
            ch.config().setAutoRead(true);
            ch.read();
        }*/
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //logger.info(cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
