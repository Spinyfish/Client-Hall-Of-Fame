/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.sctp;

import com.sun.nio.sctp.Association;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.sctp.SctpChannelConfig;
import io.netty.channel.sctp.SctpServerChannel;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

public interface SctpChannel
extends Channel {
    @Override
    public SctpServerChannel parent();

    public Association association();

    @Override
    public InetSocketAddress localAddress();

    public Set<InetSocketAddress> allLocalAddresses();

    @Override
    public SctpChannelConfig config();

    @Override
    public InetSocketAddress remoteAddress();

    public Set<InetSocketAddress> allRemoteAddresses();

    public ChannelFuture bindAddress(InetAddress var1);

    public ChannelFuture bindAddress(InetAddress var1, ChannelPromise var2);

    public ChannelFuture unbindAddress(InetAddress var1);

    public ChannelFuture unbindAddress(InetAddress var1, ChannelPromise var2);
}

