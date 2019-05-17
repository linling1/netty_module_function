package com.linling.netty.chunk;

import com.linling.netty.trafficshaping.MyServerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient {

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap client = new Bootstrap();
            client.group(group).channel(NioSocketChannel.class).handler(new MyClientInitializer());

            ChannelFuture channelFuture = client.connect("118.89.229.31", 8899).sync();
            channelFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
