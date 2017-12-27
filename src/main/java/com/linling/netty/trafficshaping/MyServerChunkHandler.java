package com.linling.netty.trafficshaping;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.handler.stream.ChunkedStream;

public class MyServerChunkHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            ByteBufInputStream in = new ByteBufInputStream(buf);
            ChunkedStream stream = new ChunkedStream(in);

            ChannelProgressivePromise progressivePromise =  ctx.channel().newProgressivePromise();
            progressivePromise.addListener(new ChannelProgressiveFutureListener(){
                @Override
                public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                    if(promise instanceof ChannelProgressivePromise) {
                        ((ChannelProgressivePromise)promise).tryProgress(progress, total);
                    }
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

            ctx.write(stream, progressivePromise);
        } else {
            super.write(ctx, msg, promise);
        }
    }
}
