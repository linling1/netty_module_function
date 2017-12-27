package com.linling.netty.trafficshaping.plain;

import com.linling.netty.trafficshaping.MyServerCommonHandler;
import io.netty.channel.ChannelHandlerContext;

public class MyServerHandlerForPlain extends MyServerCommonHandler {

    @Override
    protected void sentData(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(tempStr, getChannelProgressivePromise(ctx, future -> sentData(ctx)));
//        ctx.writeAndFlush(tempStr, getChannelProgressivePromise(ctx, null));
    }
}
