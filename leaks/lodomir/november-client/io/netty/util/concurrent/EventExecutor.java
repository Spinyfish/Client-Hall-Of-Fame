/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;

public interface EventExecutor
extends EventExecutorGroup {
    @Override
    public EventExecutor next();

    public EventExecutorGroup parent();

    public boolean inEventLoop();

    public boolean inEventLoop(Thread var1);

    public <V> Promise<V> newPromise();

    public <V> ProgressivePromise<V> newProgressivePromise();

    public <V> Future<V> newSucceededFuture(V var1);

    public <V> Future<V> newFailedFuture(Throwable var1);
}

