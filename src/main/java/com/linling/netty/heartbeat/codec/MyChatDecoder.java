package com.linling.netty.heartbeat.codec;

import com.linling.netty.heartbeat.heartbeat.MyHeartbeat;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import static com.linling.netty.heartbeat.codec.ChatProtocolState.MESSAGE_DATA;
import static com.linling.netty.heartbeat.codec.ChatProtocolState.MESSAGE_PACKAGE;
import static com.linling.netty.heartbeat.codec.ChatProtocolState.MESSAGE_TYPE;

/**
 * 自定义消息格式: | MAGIC | LENGTH | BODY |
 * MAGIC(byte) ：消息类型。{@link ChatProtocol#MAGIC_MESSAGE}表示消息类型；{@link ChatProtocol#MAGIC_HEARTBEAT_PING}表示PING心跳包；{@link ChatProtocol#MAGIC_HEARTBEAT_PONG}表示PONG心跳包
 * LENGTH(int32) ：消息长度
 * BODY(byte[]) ：消息体
 */
public class MyChatDecoder extends ReplayingDecoder<ChatProtocolState> {

    public MyChatDecoder() {
        super(MESSAGE_TYPE);
    }

    byte magic;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int length = 0;
        switch (state()) {
            case MESSAGE_TYPE:
                magic = in.readByte();
                checkpoint(MESSAGE_PACKAGE);
            case MESSAGE_PACKAGE:
                length = parseMessage(ctx, in);
                if(0 == length) {
                    break;
                }
            case MESSAGE_DATA:
                ByteBuf body = in.readBytes(length);
                out.add(body);
                checkpoint(MESSAGE_TYPE);
        }

    }

    private int parseMessage(ChannelHandlerContext ctx, ByteBuf in){
        switch (magic) {
            case ChatProtocol.MAGIC_MESSAGE:
                int length = in.readInt();
                checkpoint(MESSAGE_DATA);
                return length;
            case ChatProtocol.MAGIC_HEARTBEAT_PING:
                System.out.println("收到 " + ctx.channel().remoteAddress() + " 的 ping 包，返回一个 pong 包。");
                ctx.writeAndFlush(MyHeartbeat.getHeartbeatPongBuf());
                checkpoint(MESSAGE_TYPE);
                break;
            case ChatProtocol.MAGIC_HEARTBEAT_PONG:
                System.out.println("收到 " + ctx.channel().remoteAddress() + " 的 pong 包。");
                checkpoint(MESSAGE_TYPE);
                break;
        }
        return 0;
    }
}
