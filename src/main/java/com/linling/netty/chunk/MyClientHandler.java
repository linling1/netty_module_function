package com.linling.netty.chunk;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.time.LocalDateTime;

public class MyClientHandler extends SimpleChannelInboundHandler<String> {

    private String tempString;

    public MyClientHandler() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1024 * 1024; i++) {
            builder.append("abcdefghijklmnopqrstuvwxyz");
        }
        tempString = builder.toString();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(LocalDateTime.now().toString() + "----" + ctx.channel().remoteAddress().toString() + "----" + msg.length());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        sendData(ctx);
    }

    private void sendData(ChannelHandlerContext ctx) {
        if (!ctx.channel().isActive())
        {
            System.out.println("channel inactive...");
            ctx.close();
            return;
        }

        System.out.println("send a pack of data ...");

        long tickCount = System.currentTimeMillis();


        ChannelFuture future = ctx.writeAndFlush(tempString);
        ChannelPromise promise = (ChannelPromise)future;
        promise.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                System.out.println("send completed. isSuccess : " + future.isSuccess());
                if(!future.isSuccess()) {
                    future.cause().printStackTrace();
                }
                System.out.println("Time elapse:" + (System.currentTimeMillis() - tickCount));
//                sendData(ctx);
            }
        });




    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //System.out.println(LocalDateTime.now().toString());
        if (evt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
            System.out.println("READER_IDLE_STATE_EVENT");
        } else if (evt == IdleStateEvent.WRITER_IDLE_STATE_EVENT){
            // for heartbit
            System.out.println("WRITER_IDLE_STATE_EVENT----" + LocalDateTime.now().toString());
            //ctx.writeAndFlush("ACK");
        } else if (evt == IdleStateEvent.ALL_IDLE_STATE_EVENT) {
            //System.out.println("ALL_IDLE_STATE_EVENT");
        } else if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            System.out.println("FIRST_READER_IDLE_STATE_EVENT");
        } else if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            //System.out.println("FIRST_WRITER_IDLE_STATE_EVENT");
        } else if (evt == IdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT) {
            //System.out.println("FIRST_ALL_IDLE_STATE_EVENT");
        }
        //super.userEventTriggered(ctx, evt);
    }
}
