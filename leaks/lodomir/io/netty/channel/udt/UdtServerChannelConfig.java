/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.udt;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.udt.UdtChannelConfig;

public interface UdtServerChannelConfig
extends UdtChannelConfig {
    public int getBacklog();

    public UdtServerChannelConfig setBacklog(int var1);

    @Override
    public UdtServerChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public UdtServerChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public UdtServerChannelConfig setWriteSpinCount(int var1);

    @Override
    public UdtServerChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public UdtServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public UdtServerChannelConfig setAutoRead(boolean var1);

    @Override
    public UdtServerChannelConfig setAutoClose(boolean var1);

    @Override
    public UdtServerChannelConfig setProtocolReceiveBufferSize(int var1);

    @Override
    public UdtServerChannelConfig setProtocolSendBufferSize(int var1);

    @Override
    public UdtServerChannelConfig setReceiveBufferSize(int var1);

    @Override
    public UdtServerChannelConfig setReuseAddress(boolean var1);

    @Override
    public UdtServerChannelConfig setSendBufferSize(int var1);

    @Override
    public UdtServerChannelConfig setSoLinger(int var1);

    @Override
    public UdtServerChannelConfig setSystemReceiveBufferSize(int var1);

    @Override
    public UdtServerChannelConfig setSystemSendBufferSize(int var1);

    @Override
    public UdtServerChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public UdtServerChannelConfig setWriteBufferLowWaterMark(int var1);

    @Override
    public UdtServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

