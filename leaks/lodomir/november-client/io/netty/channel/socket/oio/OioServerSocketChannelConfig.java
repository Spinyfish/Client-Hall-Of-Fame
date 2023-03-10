/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ServerSocketChannelConfig;

public interface OioServerSocketChannelConfig
extends ServerSocketChannelConfig {
    public OioServerSocketChannelConfig setSoTimeout(int var1);

    public int getSoTimeout();

    @Override
    public OioServerSocketChannelConfig setBacklog(int var1);

    @Override
    public OioServerSocketChannelConfig setReuseAddress(boolean var1);

    @Override
    public OioServerSocketChannelConfig setReceiveBufferSize(int var1);

    @Override
    public OioServerSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3);

    @Override
    public OioServerSocketChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public OioServerSocketChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public OioServerSocketChannelConfig setWriteSpinCount(int var1);

    @Override
    public OioServerSocketChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public OioServerSocketChannelConfig setAutoRead(boolean var1);

    @Override
    public OioServerSocketChannelConfig setAutoClose(boolean var1);

    @Override
    public OioServerSocketChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public OioServerSocketChannelConfig setWriteBufferLowWaterMark(int var1);

    @Override
    public OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

