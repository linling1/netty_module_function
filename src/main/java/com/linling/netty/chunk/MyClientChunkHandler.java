package com.linling.netty.chunk;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
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
            /*
            // 使用Netty提供的ByteBufInputStream 来提高性能，底层不会再去拷贝数据。
            // 而ByteInputStream则会将ByteBuf中的数据拷贝到ByteInputStream中。并且这里此处的ByteBuf如果是堆外的内存数据的话，就更不好了，数据还需要从堆外拷贝到堆内。
            ByteInputStream in = new ByteInputStream();
            byte[] data = null;
            if(buf.hasArray()) {
                System.out.println("+++ is array");
                data = buf.array();
            } else {
                System.out.println("--- is direct");
                data = new byte[buf.readableBytes()];
                buf.writeBytes(data);

            }
            System.out.println("===== data length : " + data.length);
            in.setBuf(data);
            */


            ByteBufInputStream in = new ByteBufInputStream(buf);
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

            // 因为 ByteBufInputStream 底层引用的还是当前write操作传进来的msg（即，ByteBuf）对象。因此在使用ByteBufInputStream实现时，这里是不能调用资源释放。但在使用原先的ByteInputStream方式实现时，一定要记得调用资源释放，否则可能出现内存泄漏。
//            ReferenceCountUtil.release(msg);
            ctx.write(stream, progressivePromise);
        } else {
            super.write(ctx, msg, promise);
        }
    }
}
