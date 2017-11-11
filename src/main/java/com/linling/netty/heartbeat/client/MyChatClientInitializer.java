package com.linling.netty.heartbeat.client;

import com.linling.netty.heartbeat.codec.MyChatDecoder;
import com.linling.netty.heartbeat.codec.MyChatEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.linling.netty.heartbeat.common.MyChatContants.CLIENT_READ_TIME;


public class MyChatClientInitializer extends ChannelInitializer<SocketChannel> {

    final MyChatClient chatClient;

    Charset charset = Charset.forName("utf-8");

    public MyChatClientInitializer(MyChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast("idleStateHandler", new IdleStateHandler(CLIENT_READ_TIME, 0, 0, TimeUnit.SECONDS))
                .addLast("myChatClientIdleHandler", new MyChatClientIdleHandler(chatClient))
                .addLast("myChatDecoder", new MyChatDecoder())
                .addLast("myChatEncoder", new MyChatEncoder())
                .addLast("stringDecoder", new StringDecoder(charset))
                .addLast("stringEncoder", new StringEncoder(charset))
                .addLast("myChatClientHandler", new MyChatClientHandler());
    }
}
