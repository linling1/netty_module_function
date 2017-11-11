package com.linling.netty.heartbeat.client;

import com.linling.netty.heartbeat.heartbeat.MyHeartbeat;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.TimeUnit;

import static com.linling.netty.heartbeat.common.MyChatContants.RETRY_LIMIT;

public class MyChatClientIdleHandler extends ChannelInboundHandlerAdapter {

    private final MyChatClient chatClient;

    public MyChatClientIdleHandler(MyChatClient chatClient) {
        this.chatClient = chatClient;
    }

    private int retryCount;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state() == IdleState.READER_IDLE) {
                if(++retryCount > RETRY_LIMIT) {
                    System.out.println("server " + ctx.channel().remoteAddress() + " is inactive to close");
                    closeAndReconnection(ctx.channel());
                } else {
                    System.out.println("send ping package to " + ctx.channel().remoteAddress());
                    ctx.writeAndFlush(MyHeartbeat.getHeartbeatPingBuf());
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private void closeAndReconnection(Channel channel) {
        channel.close();
        channel.eventLoop().schedule(() -> {
            System.out.println("========== 尝试重连接 ==========");
            chatClient.connect(channel.eventLoop());
        }, 10L, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        retryCount=0;
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel() + " 已连上. 可以开始聊天...");
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered : " + ctx.channel());
        super.channelRegistered(ctx);
    }
}
