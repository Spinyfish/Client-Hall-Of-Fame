/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface ProgressiveFuture<V>
extends Future<V> {
    @Override
    public ProgressiveFuture<V> addListener(GenericFutureListener<? extends Future<? super V>> var1);

    @Override
    public ProgressiveFuture<V> addListeners(GenericFutureListener<? extends Future<? super V>> ... var1);

    @Override
    public ProgressiveFuture<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1);

    @Override
    public ProgressiveFuture<V> removeListeners(GenericFutureListener<? extends Future<? super V>> ... var1);

    @Override
    public ProgressiveFuture<V> sync() throws InterruptedException;

    @Override
    public ProgressiveFuture<V> syncUninterruptibly();

    @Override
    public ProgressiveFuture<V> await() throws InterruptedException;

    @Override
    public ProgressiveFuture<V> awaitUninterruptibly();
}

