/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractFuture<V>
implements Future<V> {
    @Override
    public V get() throws InterruptedException, ExecutionException {
        this.await();
        Throwable cause = this.cause();
        if (cause == null) {
            return this.getNow();
        }
        throw new ExecutionException(cause);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.await(timeout, unit)) {
            Throwable cause = this.cause();
            if (cause == null) {
                return this.getNow();
            }
            throw new ExecutionException(cause);
        }
        throw new TimeoutException();
    }
}

