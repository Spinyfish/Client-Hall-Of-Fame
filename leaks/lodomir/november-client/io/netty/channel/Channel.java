/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.AttributeMap;
import java.net.SocketAddress;

public interface Channel
extends AttributeMap,
Comparable<Channel> {
    public EventLoop eventLoop();

    public Channel parent();

    public ChannelConfig config();

    public boolean isOpen();

    public boolean isRegistered();

    public boolean isActive();

    public ChannelMetadata metadata();

    public SocketAddress localAddress();

    public SocketAddress remoteAddress();

    public ChannelFuture closeFuture();

    public boolean isWritable();

    public Unsafe unsafe();

    public ChannelPipeline pipeline();

    public ByteBufAllocator alloc();

    public ChannelPromise newPromise();

    public ChannelProgressivePromise newProgressivePromise();

    public ChannelFuture newSucceededFuture();

    public ChannelFuture newFailedFuture(Throwable var1);

    public ChannelPromise voidPromise();

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

    public Channel read();

    public ChannelFuture write(Object var1);

    public ChannelFuture write(Object var1, ChannelPromise var2);

    public Channel flush();

    public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

    public ChannelFuture writeAndFlush(Object var1);

    public static interface Unsafe {
        public SocketAddress localAddress();

        public SocketAddress remoteAddress();

        public void register(EventLoop var1, ChannelPromise var2);

        public void bind(SocketAddress var1, ChannelPromise var2);

        public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

        public void disconnect(ChannelPromise var1);

        public void close(ChannelPromise var1);

        public void closeForcibly();

        public void deregister(ChannelPromise var1);

        public void beginRead();

        public void write(Object var1, ChannelPromise var2);

        public void flush();

        public ChannelPromise voidPromise();

        public ChannelOutboundBuffer outboundBuffer();
    }
}

