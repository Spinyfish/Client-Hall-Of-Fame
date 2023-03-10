/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public interface ChannelPipeline
extends Iterable<Map.Entry<String, ChannelHandler>> {
    public ChannelPipeline addFirst(String var1, ChannelHandler var2);

    public ChannelPipeline addFirst(EventExecutorGroup var1, String var2, ChannelHandler var3);

    public ChannelPipeline addLast(String var1, ChannelHandler var2);

    public ChannelPipeline addLast(EventExecutorGroup var1, String var2, ChannelHandler var3);

    public ChannelPipeline addBefore(String var1, String var2, ChannelHandler var3);

    public ChannelPipeline addBefore(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4);

    public ChannelPipeline addAfter(String var1, String var2, ChannelHandler var3);

    public ChannelPipeline addAfter(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4);

    public ChannelPipeline addFirst(ChannelHandler ... var1);

    public ChannelPipeline addFirst(EventExecutorGroup var1, ChannelHandler ... var2);

    public ChannelPipeline addLast(ChannelHandler ... var1);

    public ChannelPipeline addLast(EventExecutorGroup var1, ChannelHandler ... var2);

    public ChannelPipeline remove(ChannelHandler var1);

    public ChannelHandler remove(String var1);

    public <T extends ChannelHandler> T remove(Class<T> var1);

    public ChannelHandler removeFirst();

    public ChannelHandler removeLast();

    public ChannelPipeline replace(ChannelHandler var1, String var2, ChannelHandler var3);

    public ChannelHandler replace(String var1, String var2, ChannelHandler var3);

    public <T extends ChannelHandler> T replace(Class<T> var1, String var2, ChannelHandler var3);

    public ChannelHandler first();

    public ChannelHandlerContext firstContext();

    public ChannelHandler last();

    public ChannelHandlerContext lastContext();

    public ChannelHandler get(String var1);

    public <T extends ChannelHandler> T get(Class<T> var1);

    public ChannelHandlerContext context(ChannelHandler var1);

    public ChannelHandlerContext context(String var1);

    public ChannelHandlerContext context(Class<? extends ChannelHandler> var1);

    public Channel channel();

    public List<String> names();

    public Map<String, ChannelHandler> toMap();

    public ChannelPipeline fireChannelRegistered();

    public ChannelPipeline fireChannelUnregistered();

    public ChannelPipeline fireChannelActive();

    public ChannelPipeline fireChannelInactive();

    public ChannelPipeline fireExceptionCaught(Throwable var1);

    public ChannelPipeline fireUserEventTriggered(Object var1);

    public ChannelPipeline fireChannelRead(Object var1);

    public ChannelPipeline fireChannelReadComplete();

    public ChannelPipeline fireChannelWritabilityChanged();

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

    public ChannelPipeline read();

    public ChannelFuture write(Object var1);

    public ChannelFuture write(Object var1, ChannelPromise var2);

    public ChannelPipeline flush();

    public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

    public ChannelFuture writeAndFlush(Object var1);
}

