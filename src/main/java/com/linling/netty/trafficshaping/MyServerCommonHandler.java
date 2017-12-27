package com.linling.netty.trafficshaping;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public abstract class MyServerCommonHandler extends SimpleChannelInboundHandler<String> {

    protected final int M = 1024 * 1024;
    protected String tempStr;
    protected AtomicLong consumeMsgLength;
    protected Runnable counterTask;
    private long priorProgress;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        consumeMsgLength = new AtomicLong();
        counterTask = () -> {
          while (true) {
              try {
                  Thread.sleep(1000);
              } catch (InterruptedException e) {

              }

              long length = consumeMsgLength.getAndSet(0);
              System.out.println("*** rate（M/S）：" + (length / M));
          }
        };
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < M; i++) {
            builder.append("abcdefghijklmnopqrstuvwxyz");
        }
        tempStr = builder.toString();
        super.handlerAdded(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        sentData(ctx);
        new Thread(counterTask).start();
    }

    protected ChannelProgressivePromise getChannelProgressivePromise(ChannelHandlerContext ctx, Consumer<ChannelProgressiveFuture> completedAction) {
        ChannelProgressivePromise channelProgressivePromise = ctx.newProgressivePromise();
        channelProgressivePromise.addListener(new ChannelProgressiveFutureListener(){
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                consumeMsgLength.addAndGet(progress - priorProgress);
                priorProgress = progress;
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("成功发送完成！");
                    priorProgress -= 26 * M;
                    Optional.ofNullable(completedAction).ifPresent(action -> action.accept(future));
                } else {
                    System.out.println("发送失败！！！！！");
                    future.cause().printStackTrace();
                }
            }
        });
        return channelProgressivePromise;
    }

    protected abstract void sentData(ChannelHandlerContext ctx);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("===== receive client msg : " + msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            if(((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                System.out.println("##### WRITER_IDLE #####");
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
