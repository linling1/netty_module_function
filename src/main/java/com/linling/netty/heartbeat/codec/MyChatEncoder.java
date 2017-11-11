package com.linling.netty.heartbeat.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyChatEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        out.writeByte(ChatProtocol.MAGIC_MESSAGE);
        out.writeInt(msg.readableBytes());
        out.writeBytes(msg);
    }
}
