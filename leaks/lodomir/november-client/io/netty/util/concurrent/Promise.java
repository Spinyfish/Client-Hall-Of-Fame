/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface Promise<V>
extends Future<V> {
    public Promise<V> setSuccess(V var1);

    public boolean trySuccess(V var1);

    public Promise<V> setFailure(Throwable var1);

    public boolean tryFailure(Throwable var1);

    public boolean setUncancellable();

    @Override
    public Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> var1);

    @Override
    public Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>> ... var1);

    @Override
    public Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> var1);

    @Override
    public Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>> ... var1);

    @Override
    public Promise<V> await() throws InterruptedException;

    @Override
    public Promise<V> awaitUninterruptibly();

    @Override
    public Promise<V> sync() throws InterruptedException;

    @Override
    public Promise<V> syncUninterruptibly();
}

