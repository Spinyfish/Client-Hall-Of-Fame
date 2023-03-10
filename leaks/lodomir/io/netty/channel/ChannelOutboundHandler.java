/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;

public interface ChannelOutboundHandler
extends ChannelHandler {
    public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception;

    public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception;

    public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception;

    public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception;

    public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception;

    public void read(ChannelHandlerContext var1) throws Exception;

    public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception;

    public void flush(ChannelHandlerContext var1) throws Exception;
}

