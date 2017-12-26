package com.linling.netty.chunk;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

public class MyClientChunkHandler extends ChannelOutboundHandlerAdapter {

    Charset charset = Charset.forName("utf-8");
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            ByteInputStream in = new ByteInputStream();
            byte[] data = null;
            if(buf.hasArray()) {
                data = buf.array();
            } else {
                data = new byte[buf.readableBytes()];
                buf.writeBytes(data);

            }
            System.out.println("===== data length : " + data.length);
            in.setBuf(data);
            ChunkedStream stream = new ChunkedStream(in);


            ChannelProgressivePromise progressivePromise =  ctx.channel().newProgressivePromise();
            progressivePromise.addListener(new ChannelProgressiveFutureListener(){
                @Override
                public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
//                    System.out.println("数据正在发送中。。。");
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                    if(future.isSuccess()){
                        promise.setSuccess();
                    } else {
                        promise.setFailure(future.cause());
                    }
                    System.out.println("数据已经发送完了！");
                }
            });

            ReferenceCountUtil.release(msg);
            ctx.write(stream, progressivePromise);
        } else {
            super.write(ctx, msg, promise);
        }
    }
}
