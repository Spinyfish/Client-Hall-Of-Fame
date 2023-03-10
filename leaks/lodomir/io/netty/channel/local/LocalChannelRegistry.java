/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.local;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.local.LocalAddress;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;

final class LocalChannelRegistry {
    private static final ConcurrentMap<LocalAddress, Channel> boundChannels = PlatformDependent.newConcurrentHashMap();

    static LocalAddress register(Channel channel, LocalAddress oldLocalAddress, SocketAddress localAddress) {
        Channel boundChannel;
        if (oldLocalAddress != null) {
            throw new ChannelException("already bound");
        }
        if (!(localAddress instanceof LocalAddress)) {
            throw new ChannelException("unsupported address type: " + StringUtil.simpleClassName(localAddress));
        }
        LocalAddress addr = (LocalAddress)localAddress;
        if (LocalAddress.ANY.equals(addr)) {
            addr = new LocalAddress(channel);
        }
        if ((boundChannel = boundChannels.putIfAbsent(addr, channel)) != null) {
            throw new ChannelException("address already in use by: " + boundChannel);
        }
        return addr;
    }

    static Channel get(SocketAddress localAddress) {
        return (Channel)boundChannels.get(localAddress);
    }

    static void unregister(LocalAddress localAddress) {
        boundChannels.remove(localAddress);
    }

    private LocalChannelRegistry() {
    }
}

