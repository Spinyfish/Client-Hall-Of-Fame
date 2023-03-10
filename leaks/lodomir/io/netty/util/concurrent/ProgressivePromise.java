/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;
import io.netty.util.concurrent.Promise;

public interface ProgressivePromise<V>
extends Promise<V>,
ProgressiveFuture<V> {
    public ProgressivePromise<V> setProgress(long var1, long var3);

    public boolean tryProgress(long var1, long var3);

    @Override
    public ProgressivePromise<V> setSuccess(V var1);

    @Override
    public ProgressivePromise<V> setFailure(Throwable var1);

    @Override
    public ProgressivePromise<V> addListener(GenericFutureListener<? extends Future<? super V>> var1);

    @Override
    public ProgressivePromise<V> addListeners(GenericFutureListener<? extends Future<? super V>> ... var1);

    @Override
    public ProgressivePromise<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1);

    @Override
    public ProgressivePromise<V> removeListeners(GenericFutureListener<? extends Future<? super V>> ... var1);

    @Override
    public ProgressivePromise<V> await() throws InterruptedException;

    @Override
    public ProgressivePromise<V> awaitUninterruptibly();

    @Override
    public ProgressivePromise<V> sync() throws InterruptedException;

    @Override
    public ProgressivePromise<V> syncUninterruptibly();
}

