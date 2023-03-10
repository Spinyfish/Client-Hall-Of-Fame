/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.EventExecutorGroup;

public interface EventLoopGroup
extends EventExecutorGroup {
    @Override
    public EventLoop next();

    public ChannelFuture register(Channel var1);

    public ChannelFuture register(Channel var1, ChannelPromise var2);
}

