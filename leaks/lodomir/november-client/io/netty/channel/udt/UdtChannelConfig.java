/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.udt;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface UdtChannelConfig
extends ChannelConfig {
    public int getProtocolReceiveBufferSize();

    public int getProtocolSendBufferSize();

    public int getReceiveBufferSize();

    public int getSendBufferSize();

    public int getSoLinger();

    public int getSystemReceiveBufferSize();

    public int getSystemSendBufferSize();

    public boolean isReuseAddress();

    @Override
    public UdtChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public UdtChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public UdtChannelConfig setWriteSpinCount(int var1);

    @Override
    public UdtChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public UdtChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public UdtChannelConfig setAutoRead(boolean var1);

    @Override
    public UdtChannelConfig setAutoClose(boolean var1);

    @Override
    public UdtChannelConfig setWriteBufferHighWaterMark(int var1);

    @Override
    public UdtChannelConfig setWriteBufferLowWaterMark(int var1);

    @Override
    public UdtChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);

    public UdtChannelConfig setProtocolReceiveBufferSize(int var1);

    public UdtChannelConfig setProtocolSendBufferSize(int var1);

    public UdtChannelConfig setReceiveBufferSize(int var1);

    public UdtChannelConfig setReuseAddress(boolean var1);

    public UdtChannelConfig setSendBufferSize(int var1);

    public UdtChannelConfig setSoLinger(int var1);

    public UdtChannelConfig setSystemReceiveBufferSize(int var1);

    public UdtChannelConfig setSystemSendBufferSize(int var1);
}

