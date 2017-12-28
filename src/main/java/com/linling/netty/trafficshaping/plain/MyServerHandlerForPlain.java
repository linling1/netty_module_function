package com.linling.netty.trafficshaping.plain;

import com.linling.netty.trafficshaping.MyServerCommonHandler;
import io.netty.channel.ChannelHandlerContext;

public class MyServerHandlerForPlain extends MyServerCommonHandler {

    @Override
    protected void sentData(ChannelHandlerContext ctx) {
        sentFlag = true;
        ctx.writeAndFlush(tempStr, getChannelProgressivePromise(ctx, future -> sentData(ctx)));
//        ctx.writeAndFlush(tempStr, getChannelProgressivePromise(ctx, null));
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if(ctx.channel().isWritable() && !sentFlag) {
            System.out.println(" ###### 重新开始写数据 ######");
//            sentData(ctx);
        }
    }
}
