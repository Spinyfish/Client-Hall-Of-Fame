/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeMap;
import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;

public interface ChannelHandlerContext
extends AttributeMap {
    public Channel channel();

    public EventExecutor executor();

    public String name();

    public ChannelHandler handler();

    public boolean isRemoved();

    public ChannelHandlerContext fireChannelRegistered();

    public ChannelHandlerContext fireChannelUnregistered();

    public ChannelHandlerContext fireChannelActive();

    public ChannelHandlerContext fireChannelInactive();

    public ChannelHandlerContext fireExceptionCaught(Throwable var1);

    public ChannelHandlerContext fireUserEventTriggered(Object var1);

    public ChannelHandlerContext fireChannelRead(Object var1);

    public ChannelHandlerContext fireChannelReadComplete();

    public ChannelHandlerContext fireChannelWritabilityChanged();

    public ChannelFuture bind(SocketAddress var1);

    public ChannelFuture connect(SocketAddress var1);

    public ChannelFuture connect(SocketAddress var1, SocketAddress var2);

    public ChannelFuture disconnect();

    public ChannelFuture close();

    public ChannelFuture deregister();

    public ChannelFuture bind(SocketAddress var1, ChannelPromise var2);

    public ChannelFuture connect(SocketAddress var1, ChannelPromise var2);

    public ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

    public ChannelFuture disconnect(ChannelPromise var1);

    public ChannelFuture close(ChannelPromise var1);

    public ChannelFuture deregister(ChannelPromise var1);

    public ChannelHandlerContext read();

    public ChannelFuture write(Object var1);

    public ChannelFuture write(Object var1, ChannelPromise var2);

    public ChannelHandlerContext flush();

    public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

    public ChannelFuture writeAndFlush(Object var1);

    public ChannelPipeline pipeline();

    public ByteBufAllocator alloc();

    public ChannelPromise newPromise();

    public ChannelProgressivePromise newProgressivePromise();

    public ChannelFuture newSucceededFuture();

    public ChannelFuture newFailedFuture(Throwable var1);

    public ChannelPromise voidPromise();
}

