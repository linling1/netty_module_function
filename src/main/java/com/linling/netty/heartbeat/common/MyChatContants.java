package com.linling.netty.heartbeat.common;

public class MyChatContants {

    public static final int RETRY_LIMIT = 3;

    public static final long CLIENT_READ_TIME = 10;

    public static final long SERVER_READ_TIME = 15 * RETRY_LIMIT;
}
