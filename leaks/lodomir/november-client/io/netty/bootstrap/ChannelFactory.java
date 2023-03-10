/*
 * Decompiled with CFR 0.152.
 */
package io.netty.bootstrap;

import io.netty.channel.Channel;

public interface ChannelFactory<T extends Channel> {
    public T newChannel();
}

