/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.util.concurrent.EventExecutor;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;

public class NioEventLoopGroup
extends MultithreadEventLoopGroup {
    public NioEventLoopGroup() {
        this(0);
    }

    public NioEventLoopGroup(int nThreads) {
        this(nThreads, null);
    }

    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        this(nThreads, threadFactory, SelectorProvider.provider());
    }

    public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectorProvider selectorProvider) {
        super(nThreads, threadFactory, selectorProvider);
    }

    public void setIoRatio(int ioRatio) {
        for (EventExecutor e : this.children()) {
            ((NioEventLoop)e).setIoRatio(ioRatio);
        }
    }

    public void rebuildSelectors() {
        for (EventExecutor e : this.children()) {
            ((NioEventLoop)e).rebuildSelector();
        }
    }

    @Override
    protected EventExecutor newChild(ThreadFactory threadFactory, Object ... args) throws Exception {
        return new NioEventLoop(this, threadFactory, (SelectorProvider)args[0]);
    }
}

