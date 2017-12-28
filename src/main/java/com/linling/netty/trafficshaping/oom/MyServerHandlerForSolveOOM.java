package com.linling.netty.trafficshaping.oom;

import com.linling.netty.trafficshaping.MyServerCommonHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 因为写操作是一个I/O操作，当你在非NioEventLoop线程上执行了Channel的I/O操作的话，该操作会封装为一个task 被提交至NioEventLoop的任务队列中，以使得I/O操作最终是NioEventLoop线程上得到执行。
 而提交这个任务的流程，仅会对ByteBuf、ByteBufHolder或者FileRegion对象进行真实数据大小的估计（其他情况默认估计大小为8 bytes），并将估计后的数据大小值对该ChannelOutboundBuffer的totalPendingSize属性值进行累加。而totalPendingSize同WriteBufferWaterMark一起来控制着Channel的unwritable。所以，如果你在一个非NioEventLoop线程中不断地发送一个非ByteBuf、ByteBufHolder或者FileRegion对象的大数据包时，最终就会导致NioEventLoop线程在真实执行这些task时发送OOM。
 * 【Caused by: io.netty.util.internal.OutOfDirectMemoryError: failed to allocate 81788928 byte(s) of direct memory (used: 916455424, max: 954728448)】
 */
public class MyServerHandlerForSolveOOM extends MyServerCommonHandler {

    @Override
    protected void sentData(ChannelHandlerContext ctx) {

//        new Thread(() -> {
            while (true) {
                if(ctx.channel().isWritable()) {
                    System.out.println("===");
                    ctx.writeAndFlush(tempStr, getChannelProgressivePromise(ctx, null));
                } else {
                    System.out.println("#######");
                    break;
                }
            }
//        }).start();
    }


}
