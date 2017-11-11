package com.linling.netty.heartbeat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class MyChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String msg = channel.remoteAddress() + " 上线！";
        System.out.println(msg);
        channelGroup.writeAndFlush(msg);
        channelGroup.add(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("收到" + channel.remoteAddress() + " 消息 : " + msg);
        String sendMsg = "[" + channel.remoteAddress() + "] : " + msg;
        channelGroup.writeAndFlush(sendMsg, c -> !channel.remoteAddress().equals(c.remoteAddress()));
        String myMsg = "[自己] : " + msg;
        channel.writeAndFlush(myMsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String msg = channel.remoteAddress() + " 下线！";
        System.out.println(msg);
        channelGroup.writeAndFlush(msg);
    }
}
