package com.linling.netty.trafficshaping.oom;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

import java.util.concurrent.ScheduledExecutorService;

@ChannelHandler.Sharable
public class MyGlobalTrafficShapingHandler extends GlobalTrafficShapingHandler {

    public MyGlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit,
                                       long readLimit) {
        super(executor, writeLimit, readLimit);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        System.out.println("msg : " + msg);
        System.out.println("thread : " + Thread.currentThread() + " , queuesSize : " + queuesSize() + " , maxWriteSize " + getMaxWriteSize() + " , maxGlobalWriteSize " + getMaxGlobalWriteSize());

    }
}
