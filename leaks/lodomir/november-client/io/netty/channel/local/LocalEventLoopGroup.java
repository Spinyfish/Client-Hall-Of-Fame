/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.local;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.local.LocalEventLoop;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.ThreadFactory;

public class LocalEventLoopGroup
extends MultithreadEventLoopGroup {
    public LocalEventLoopGroup() {
        this(0);
    }

    public LocalEventLoopGroup(int nThreads) {
        this(nThreads, null);
    }

    public LocalEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        super(nThreads, threadFactory, new Object[0]);
    }

    @Override
    protected EventExecutor newChild(ThreadFactory threadFactory, Object ... args) throws Exception {
        return new LocalEventLoop(this, threadFactory);
    }
}

