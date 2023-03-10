/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import java.net.InetAddress;
import java.net.NetworkInterface;

public interface DatagramChannelConfig
extends ChannelConfig {
    public int getSendBufferSize();

    public DatagramChannelConfig setSendBufferSize(int var1);

    public int getReceiveBufferSize();

    public DatagramChannelConfig setReceiveBufferSize(int var1);

    public int getTrafficClass();

    public DatagramChannelConfig setTrafficClass(int var1);

    public boolean isReuseAddress();

    public DatagramChannelConfig setReuseAddress(boolean var1);

    public boolean isBroadcast();

    public DatagramChannelConfig setBroadcast(boolean var1);

    public boolean isLoopbackModeDisabled();

    public DatagramChannelConfig setLoopbackModeDisabled(boolean var1);

    public int getTimeToLive();

    public DatagramChannelConfig setTimeToLive(int var1);

    public InetAddress getInterface();

    public DatagramChannelConfig setInterface(InetAddress var1);

    public NetworkInterface getNetworkInterface();

    public DatagramChannelConfig setNetworkInterface(NetworkInterface var1);

    @Override
    public DatagramChannelConfig setMaxMessagesPerRead(int var1);

    @Override
    public DatagramChannelConfig setWriteSpinCount(int var1);

    @Override
    public DatagramChannelConfig setConnectTimeoutMillis(int var1);

    @Override
    public DatagramChannelConfig setAllocator(ByteBufAllocator var1);

    @Override
    public DatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

    @Override
    public DatagramChannelConfig setAutoRead(boolean var1);

    @Override
    public DatagramChannelConfig setAutoClose(boolean var1);

    @Override
    public DatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}

