package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Request;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import com.alibaba.dubbo.performance.demo.agent.provider.ChannelCountMapUtil;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class DubboRpcHandler extends ChannelOutboundHandlerAdapter {
    // header length.
    protected static final int HEADER_LENGTH = 16;
    // magic header.
    protected static final short MAGIC = (short) 0xdabb;
    // message flag.
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        Request req = (Request)msg;
        // header.
        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);

        // set request and serialization flag.
        header[2] = (byte) (FLAG_REQUEST | 6);

        if (req.isTwoWay()) header[2] |= FLAG_TWOWAY;
        if (req.isEvent()) header[2] |= FLAG_EVENT;

        // set request id.
        Bytes.long2bytes(req.getId(), header, 4);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        encodeRequestData(bos, req.getData());

        int len = bos.size();
        byte[] body = bos.toByteArray();
        Bytes.int2bytes(len, header, 12);

        // write

        ByteBuf byteBuf= Unpooled.wrappedBuffer(header,body);
		ctx.writeAndFlush(byteBuf,ctx.voidPromise());		
		/*		
		ChannelCountMapUtil.addCount(ctx.channel());
		if(ChannelCountMapUtil.getCount(ctx.channel()) >= 3){
			ctx.flush();
			ChannelCountMapUtil.ResetCount(ctx.channel());
		}*/
        
    }
    public void encodeRequestData(OutputStream out, Object data) throws Exception {
        RpcInvocation inv = (RpcInvocation)data;

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));

        JsonUtils.writeObject(inv.getAttachment("dubbo", "2.0.1"), writer);
        JsonUtils.writeObject(inv.getAttachment("path"), writer);
        JsonUtils.writeObject(inv.getAttachment("version"), writer);
        JsonUtils.writeObject(inv.getMethodName(), writer);
        JsonUtils.writeObject(inv.getParameterTypes(), writer);

        JsonUtils.writeBytes(inv.getArguments(), writer);
        JsonUtils.writeObject(inv.getAttachments(), writer);
    }
}
