package com.linling.netty.trafficshaping;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

import java.nio.charset.Charset;

public class MyClientInitializer extends ChannelInitializer<SocketChannel> {

    Charset utf8 = Charset.forName("utf-8");

    final int M = 1024 * 1024;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelTrafficShapingHandler channelTrafficShapingHandler = new ChannelTrafficShapingHandler(10 * M, 1 * M);
        ch.pipeline()
                .addLast("channelTrafficShapingHandler",channelTrafficShapingHandler)
                .addLast("lengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4, true))
                .addLast("lengthFieldPrepender", new LengthFieldPrepender(4, 0))
                .addLast("stringDecoder", new StringDecoder(utf8))
                .addLast("stringEncoder", new StringEncoder(utf8))
                .addLast("myClientHandler", new MyClientHandler());
    }
}
