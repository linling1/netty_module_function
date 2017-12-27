package com.linling.netty.trafficshaping;

import com.linling.netty.trafficshaping.oom.MyServerInitializerForSolveOOM;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import static io.netty.channel.ChannelOption.WRITE_BUFFER_WATER_MARK;

public class MyServer {

    final static int M = 1024 * 1024;

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new MyServerInitializerForSolveOOM());

            server.childOption(WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(30 * M, 50 * M));

            ChannelFuture channelFuture = server.bind(5566).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
