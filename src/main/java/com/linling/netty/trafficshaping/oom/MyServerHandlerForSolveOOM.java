package com.linling.netty.trafficshaping.oom;

import com.linling.netty.trafficshaping.MyServerCommonHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 因为，对端限制了接收带宽为 1 M/s。所以write出去的数据会积压在ChannelOutboundBuffer中，ChannelOutboundBuffer是一个无限长缓存区。
 * 而数据的写出速率有被限制住了（对端接收带宽限制为了 1 M/s，这样当对端的接收缓冲区满的话，TCP的滑动窗口就会变为0，这样本段也就不会继续发送数据了 ），导致待发送的数据会积压在ChannelOutboundBuffer中，然后新的write操作又会不断从池中申请内存（旧的数据一致未被写出去，内存一直被占用的），最终导致OOM。
 * 【Caused by: io.netty.util.internal.OutOfDirectMemoryError: failed to allocate 81788928 byte(s) of direct memory (used: 916455424, max: 954728448)】
 */
public class MyServerHandlerForSolveOOM extends MyServerCommonHandler {

    @Override
    protected void sentData(ChannelHandlerContext ctx) {

        new Thread(() -> {
            while (true) {
                if(ctx.channel().isWritable()) {
                    System.out.println("=======");
                    ctx.writeAndFlush(tempStr, getChannelProgressivePromise(ctx, null));
                }
            }
        }).start();

    }


}
