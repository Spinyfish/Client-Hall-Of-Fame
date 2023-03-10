/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface ServerSocketChannelConfig
extends ChannelConfig {
    public int getBacklog();

    public ServerSocketChannelConfig setBacklog(int var1);

    public boolean isReuseAddress();

    public ServerSocketChannelConfig setReuseAddress(boolean var1);

    public int getReceiveBufferSize();

    public ServerSocketChannelConfig setReceiveBufferSize(int var1);

    public ServerSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3);

    @Override
    public ServerSocketChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public ServerSocketChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public ServerSocketChannelConfig setWriteSpinCount(int var1);

    @Override
    public ServerSocketChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public ServerSocketChannelConfig setAutoRead(boolean var1);

    @Override
    public ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

