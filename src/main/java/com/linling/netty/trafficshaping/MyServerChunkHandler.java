package com.linling.netty.trafficshaping;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.ReferenceCountUtil;

public class MyServerChunkHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            ByteInputStream in = new ByteInputStream();
            byte[] data = null;
            if(buf.hasArray()) {
                System.out.println("+++ is array");
                data = buf.array().clone();
            } else {
                System.out.println("--- is direct");
                data = new byte[buf.readableBytes()];
                buf.writeBytes(data);

            }
//            System.out.println("===== data length : " + data.length);
            in.setBuf(data);
            ChunkedStream stream = new ChunkedStream(in);

            ReferenceCountUtil.release(msg);
            ctx.write(stream, promise);
        } else {
            super.write(ctx, msg, promise);
        }
    }
}
