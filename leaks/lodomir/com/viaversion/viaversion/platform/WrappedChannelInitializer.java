/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.platform;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public interface WrappedChannelInitializer {
    public ChannelInitializer<Channel> original();
}

