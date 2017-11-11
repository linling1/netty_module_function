package com.linling.netty.heartbeat.heartbeat;

import com.linling.netty.heartbeat.codec.ChatProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MyHeartbeat {

    private static final ByteBuf HEARTBEAT_PING_BUF;
    private static final ByteBuf HEARTBEAT_PONG_BUF;

    static {
        ByteBuf pingBuf = Unpooled.buffer();
        pingBuf.writeByte(ChatProtocol.MAGIC_HEARTBEAT_PING);
        HEARTBEAT_PING_BUF = Unpooled.unreleasableBuffer(pingBuf).asReadOnly();

        ByteBuf pongBuf = Unpooled.buffer();
        pongBuf.writeByte(ChatProtocol.MAGIC_HEARTBEAT_PONG);
        HEARTBEAT_PONG_BUF = Unpooled.unreleasableBuffer(pongBuf).asReadOnly();
    }

    public static ByteBuf getHeartbeatPingBuf() {
        return HEARTBEAT_PING_BUF;
    }

    public static ByteBuf getHeartbeatPongBuf() {
        return HEARTBEAT_PONG_BUF;
    }
}
