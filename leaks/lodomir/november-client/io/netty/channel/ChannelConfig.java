/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import java.util.Map;

public interface ChannelConfig {
    public Map<ChannelOption<?>, Object> getOptions();

    public boolean setOptions(Map<ChannelOption<?>, ?> var1);

    public <T> T getOption(ChannelOption<T> var1);

    public <T> boolean setOption(ChannelOption<T> var1, T var2);

    public int getConnectTimeoutMillis();

    public ChannelConfig setConnectTimeoutMillis(int var1);

    public int getMaxMessagesPerRead();

    public ChannelConfig setMaxMessagesPerRead(int var1);

    public int getWriteSpinCount();

    public ChannelConfig setWriteSpinCount(int var1);

    public ByteBufAllocator getAllocator();

    public ChannelConfig setAllocator(ByteBufAllocator var1);

    public RecvByteBufAllocator getRecvByteBufAllocator();

    public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    public boolean isAutoRead();

    public ChannelConfig setAutoRead(boolean var1);

    @Deprecated
    public boolean isAutoClose();

    @Deprecated
    public ChannelConfig setAutoClose(boolean var1);

    public int getWriteBufferHighWaterMark();

    public ChannelConfig setWriteBufferHighWaterMark(int var1);

    public int getWriteBufferLowWaterMark();

    public ChannelConfig setWriteBufferLowWaterMark(int var1);

    public MessageSizeEstimator getMessageSizeEstimator();

    public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

