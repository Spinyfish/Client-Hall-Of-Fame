/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.SocketChannelConfig;

public interface OioSocketChannelConfig
extends SocketChannelConfig {
    public OioSocketChannelConfig setSoTimeout(int var1);

    public int getSoTimeout();

    @Override
    public OioSocketChannelConfig setTcpNoDelay(boolean var1);

    @Override
    public OioSocketChannelConfig setSoLinger(int var1);

    @Override
    public OioSocketChannelConfig setSendBufferSize(int var1);

    @Override
    public OioSocketChannelConfig setReceiveBufferSize(int var1);

    @Override
    public OioSocketChannelConfig setKeepAlive(boolean var1);

    @Override
    public OioSocketChannelConfig setTrafficClass(int var1);

    @Override
    public OioSocketChannelConfig setReuseAddress(boolean var1);

    @Override
    public OioSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3);

    @Override
    public OioSocketChannelConfig setAllowHalfClosure(boolean var1);

    @Override
    public OioSocketChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public OioSocketChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public OioSocketChannelConfig setWriteSpinCount(int var1);

    @Override
    public OioSocketChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public OioSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public OioSocketChannelConfig setAutoRead(boolean var1);

    @Override
    public OioSocketChannelConfig setAutoClose(boolean var1);

    @Override
    public OioSocketChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public OioSocketChannelConfig setWriteBufferLowWaterMark(int var1);

    @Override
    public OioSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

