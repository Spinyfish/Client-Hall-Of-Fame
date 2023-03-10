/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface SocketChannelConfig
extends ChannelConfig {
    public boolean isTcpNoDelay();

    public SocketChannelConfig setTcpNoDelay(boolean var1);

    public int getSoLinger();

    public SocketChannelConfig setSoLinger(int var1);

    public int getSendBufferSize();

    public SocketChannelConfig setSendBufferSize(int var1);

    public int getReceiveBufferSize();

    public SocketChannelConfig setReceiveBufferSize(int var1);

    public boolean isKeepAlive();

    public SocketChannelConfig setKeepAlive(boolean var1);

    public int getTrafficClass();

    public SocketChannelConfig setTrafficClass(int var1);

    public boolean isReuseAddress();

    public SocketChannelConfig setReuseAddress(boolean var1);

    public SocketChannelConfig setPerformancePreferences(int var1, int var2, int var3);

    public boolean isAllowHalfClosure();

    public SocketChannelConfig setAllowHalfClosure(boolean var1);

    @Override
    public SocketChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public SocketChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public SocketChannelConfig setWriteSpinCount(int var1);

    @Override
    public SocketChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public SocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public SocketChannelConfig setAutoRead(boolean var1);

    @Override
    public SocketChannelConfig setAutoClose(boolean var1);

    @Override
    public SocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

