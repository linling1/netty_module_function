package com.linling.netty.heartbeat.codec;

import com.linling.netty.heartbeat.heartbeat.MyHeartbeat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 自定义消息格式: | MAGIC | LENGTH | BODY |
 * MAGIC(byte) ：消息类型。{@link ChatProtocol#MAGIC_MESSAGE}表示消息类型；{@link ChatProtocol#MAGIC_HEARTBEAT_PING}表示PING心跳包；{@link ChatProtocol#MAGIC_HEARTBEAT_PONG}表示PONG心跳包
 * LENGTH(int32) ：消息长度
 * BODY(byte[]) ：消息体
 */
public class MyChatDecoder extends ReplayingDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte magic = in.readByte();
        switch (magic) {
            case ChatProtocol.MAGIC_MESSAGE:
                int length = in.readInt();
                ByteBuf body = in.readBytes(length);
                out.add(body);
                break;
            case ChatProtocol.MAGIC_HEARTBEAT_PING:
                System.out.println("收到 " + ctx.channel().remoteAddress() + " 的 ping 包，返回一个 pong 包。");
                ctx.writeAndFlush(MyHeartbeat.getHeartbeatPongBuf());
                break;
            case ChatProtocol.MAGIC_HEARTBEAT_PONG:
                System.out.println("收到 " + ctx.channel().remoteAddress() + " 的 pong 包。");
                break;
        }

    }
}
