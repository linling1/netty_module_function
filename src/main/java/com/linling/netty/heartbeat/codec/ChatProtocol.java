package com.linling.netty.heartbeat.codec;

public class ChatProtocol {

    public static final byte MAGIC_MESSAGE = 1;
    public static final byte MAGIC_HEARTBEAT_PING = 2;
    public static final byte MAGIC_HEARTBEAT_PONG = 3;
}
