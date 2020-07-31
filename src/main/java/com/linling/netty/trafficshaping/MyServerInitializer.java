package com.linling.netty.trafficshaping;

import com.linling.netty.trafficshaping.oom.MyServerHandlerForOOM;
import com.linling.netty.trafficshaping.oom.MyServerHandlerForSolveOOM;
import com.linling.netty.trafficshaping.plain.MyServerHandlerForPlain;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;

import java.nio.charset.Charset;

public class MyServerInitializer extends ChannelInitializer<SocketChannel> {

    Charset utf8 = Charset.forName("utf-8");
    final int M = 1024 * 1024;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(ch.eventLoop().parent(), 10 * M, 50 * M);
//        globalTrafficShapingHandler.setMaxGlobalWriteSize(50 * M);
//        globalTrafficShapingHandler.setMaxWriteSize(5 * M);

        ch.pipeline()
                .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4, true))
                .addLast("LengthFieldPrepender", new LengthFieldPrepender(4, 0))
                .addLast("GlobalTrafficShapingHandler", globalTrafficShapingHandler)
                .addLast("chunkedWriteHandler", new ChunkedWriteHandler())
                .addLast("myServerChunkHandler", new MyServerChunkHandler())
                .addLast("StringDecoder", new StringDecoder(utf8))
                .addLast("StringEncoder", new StringEncoder(utf8))
                .addLast("myServerHandler", new MyServerHandlerForPlain());
//                .addLast("myServerHandler", new MyServerHandlerForOOM());
//                .addLast("myServerHandler", new MyServerHandlerForSolveOOM());
    }
}
