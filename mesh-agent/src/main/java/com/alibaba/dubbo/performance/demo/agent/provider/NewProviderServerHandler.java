package com.alibaba.dubbo.performance.demo.agent.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClientInitializer;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Request;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.AreaAveragingScaleFilter;
import java.net.InetSocketAddress;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Arrays;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class NewProviderServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private Logger logger = LoggerFactory.getLogger(NewProviderServerHandler.class);
    private Channel rpcChannel;
    private ChannelFuture connectFuture;
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //EpollSocketChannel.class
        //NioSocketChannel.class
       // logger.info("Server channelActive");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(EpollSocketChannel.class)
                .handler(new RpcClientInitializer());
        bootstrap.group(ctx.channel().eventLoop());
        connectFuture  = bootstrap.connect(new InetSocketAddress("127.0.0.1",20880));
        RpcChannelUtil.getMap().put(connectFuture.channel(),ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
            byteBuf.readerIndex(4+byteBuf.readerIndex());
            long reqId = byteBuf.readLong();
            String str = byteBuf.toString(io.netty.util.CharsetUtil.UTF_8);
            /*
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/?"+str);
            Map<String, List<String>> params = queryStringDecoder.parameters();
            logger.info("是否能解析出参数"+String.valueOf(params == null));
            */
			
/*            Map<String,String> paramMap = new HashMap<>();
            if (!params.isEmpty()) {
                for (Entry<String, List<String>> p: params.entrySet()) {
                    String key = p.getKey();
                    System.out.println("key:::"+key);
                    List<String> vals = p.getValue();
                    for (String val : vals) {
                        paramMap.put(key,val);
                    }
                }
            }*/
                String[] param = str.split("&");

                String interface_name = param[0].split("=")[1];
                String method_name = param[1].split("=")[1];
                String parameterTypesString = URLDecoder.decode(param[2].split("=")[1],"UTF-8");   ;
                String[] parameterarr = param[3].split("=");
                String parameter;
                if(parameterarr.length == 1){
                    parameter = "";
                }else{
                    parameter = parameterarr[1];
                }
            RpcInvocation invocation = new RpcInvocation();
            invocation.setMethodName(method_name);
            invocation.setAttachment("path", interface_name);
            invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

            ByteArrayOutputStream out = new ByteArrayOutputStream();//字节数组输出流在内存中创建一个字节数组缓冲区，所有发送到输出流的数据保存在该字节数组缓冲区中。
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
            JsonUtils.writeObject(parameter, writer);
            invocation.setArguments(out.toByteArray());

            com.alibaba.dubbo.performance.demo.agent.dubbo.model.Request rpcRequest = new Request();
            rpcRequest.setVersion("2.0.0");
            rpcRequest.setTwoWay(true);
            rpcRequest.setData(invocation);
            rpcRequest.setId(reqId);
			/*
			long size = CountRequestUtil.add();
            if(size > 200){
                logger.info("cannot read "+ size);
                ctx.channel().config().setAutoRead(false);
            }*/


            connectFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    rpcChannel = connectFuture.channel();
                    rpcChannel.write(rpcRequest,rpcChannel.voidPromise());
                }
            });



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}