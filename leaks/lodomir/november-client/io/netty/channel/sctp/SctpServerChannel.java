/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.sctp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ServerChannel;
import io.netty.channel.sctp.SctpServerChannelConfig;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

public interface SctpServerChannel
extends ServerChannel {
    @Override
    public SctpServerChannelConfig config();

    @Override
    public InetSocketAddress localAddress();

    public Set<InetSocketAddress> allLocalAddresses();

    public ChannelFuture bindAddress(InetAddress var1);

    public ChannelFuture bindAddress(InetAddress var1, ChannelPromise var2);

    public ChannelFuture unbindAddress(InetAddress var1);

    public ChannelFuture unbindAddress(InetAddress var1, ChannelPromise var2);
}

