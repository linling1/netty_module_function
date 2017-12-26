package com.linling.netty.chunk;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

public class MyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addFirst("idleStateHandler", new IdleStateHandler(9, 1, 11, TimeUnit.SECONDS));
        pipeline.addLast("lengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE,
                0, 4, 0, 4, true));
        pipeline.addLast("lengthFieldPrepender", new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 4, 0, false));
        pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
        pipeline.addLast("myClientChunkHandler", new MyClientChunkHandler());
        pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast("myClientHandler", new MyClientHandler());
    }
}
