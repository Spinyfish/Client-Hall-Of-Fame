/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;

public interface EventLoop
extends EventExecutor,
EventLoopGroup {
    @Override
    public EventLoopGroup parent();
}

