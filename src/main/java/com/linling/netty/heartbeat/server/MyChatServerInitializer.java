package com.linling.netty.heartbeat.server;

import com.linling.netty.heartbeat.codec.MyChatDecoder;
import com.linling.netty.heartbeat.codec.MyChatEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.linling.netty.heartbeat.common.MyChatContants.SERVER_READ_TIME;

public class MyChatServerInitializer extends ChannelInitializer<SocketChannel> {

    Charset charset = Charset.forName("utf-8");

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast("idleStateHandler", new IdleStateHandler(SERVER_READ_TIME, 0, 0, TimeUnit.SECONDS))
                .addLast("myChatServerIdleHandler", new MyChatServerIdleHandler())
                .addLast("myChatDecoder", new MyChatDecoder())
                .addLast("myChatEncoder", new MyChatEncoder())
                .addLast("stringDecoder", new StringDecoder(charset))
                .addLast("stringEncoder", new StringEncoder(charset))
                .addLast("myChatServerHandler", new MyChatServerHandler());
    }
}
