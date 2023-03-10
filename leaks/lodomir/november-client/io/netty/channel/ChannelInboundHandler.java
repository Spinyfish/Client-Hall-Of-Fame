/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public interface ChannelInboundHandler
extends ChannelHandler {
    public void channelRegistered(ChannelHandlerContext var1) throws Exception;

    public void channelUnregistered(ChannelHandlerContext var1) throws Exception;

    public void channelActive(ChannelHandlerContext var1) throws Exception;

    public void channelInactive(ChannelHandlerContext var1) throws Exception;

    public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception;

    public void channelReadComplete(ChannelHandlerContext var1) throws Exception;

    public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception;

    public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception;

    @Override
    public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception;
}

