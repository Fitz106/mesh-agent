package com.alibaba.dubbo.performance.demo.agent.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClientInitializer;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import io.netty.handler.codec.http.QueryStringDecoder;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
@ChannelHandler.Sharable
public class SecodProviderServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final InetSocketAddress dubboAddress = new InetSocketAddress("127.0.0.1",20880);
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if( !RpcEventLoopUtil.getMap().containsKey(ctx.channel().eventLoop())){
            RpcNettyPoolClient nettyPoolClient = new RpcNettyPoolClient();
            nettyPoolClient.build(ctx.channel().eventLoop());
            nettyPoolClient.poolMap.get(dubboAddress);
            RpcEventLoopUtil.getMap().put(ctx.channel().eventLoop(),nettyPoolClient);
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        byteBuf.readerIndex(4+byteBuf.readerIndex());
        long reqId = byteBuf.readLong();
        String str = byteBuf.toString(io.netty.util.CharsetUtil.UTF_8);
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/?"+str);
            Map<String, List<String>> params = queryStringDecoder.parameters();
            String method_name = null;
            String interface_name = null;
            String parameterTypesString = null;
            String parameter = null;
            if (!params.isEmpty()) {
                for (Map.Entry<String, List<String>> p: params.entrySet()) {
                    String keyName = p.getKey();
                    if("interface".equals(keyName)){
                        interface_name = p.getValue().get(0);
                
                    }else if("method".equals(keyName)){
                        method_name = p.getValue().get(0);
                    
                    }else if("parameterTypesString".equals(keyName)){
                        parameterTypesString = p.getValue().get(0);
                     
                    }else {
                        parameter = p.getValue().get(0);
                     
                    }
                }
            }
        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(method_name);
        invocation.setAttachment("path", interface_name);
        invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

        ByteArrayOutputStream out = new ByteArrayOutputStream();//字节数组输出流在内存中创建一个字节数组缓冲区，所有发送到输出流的数据保存在该字节数组缓冲区中。
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(parameter, writer);
        invocation.setArguments(out.toByteArray());

        Request rpcRequest = new Request();
        rpcRequest.setVersion("2.0.0");
        rpcRequest.setTwoWay(true);
        rpcRequest.setData(invocation);
        rpcRequest.setId(reqId);

        RpcRequestAndSessionHolder.put(String.valueOf(reqId),ctx.channel());
        SimpleChannelPool pool = RpcEventLoopUtil.getMap().get(ctx.channel().eventLoop()).poolMap.get(dubboAddress);
        Future<Channel> f = pool.acquire();
        f.addListener((FutureListener<Channel>) f1 -> {
            if (f1.isSuccess()) {
                Channel ch = f1.getNow();
				ch.write(rpcRequest,ch.voidPromise());
                pool.release(ch);
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