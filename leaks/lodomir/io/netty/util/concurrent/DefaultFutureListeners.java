/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GenericProgressiveFutureListener;
import java.util.Arrays;

final class DefaultFutureListeners {
    private GenericFutureListener<? extends Future<?>>[] listeners = new GenericFutureListener[2];
    private int size;
    private int progressiveSize;

    DefaultFutureListeners(GenericFutureListener<? extends Future<?>> first, GenericFutureListener<? extends Future<?>> second) {
        this.listeners[0] = first;
        this.listeners[1] = second;
        this.size = 2;
        if (first instanceof GenericProgressiveFutureListener) {
            ++this.progressiveSize;
        }
        if (second instanceof GenericProgressiveFutureListener) {
            ++this.progressiveSize;
        }
    }

    public void add(GenericFutureListener<? extends Future<?>> l) {
        int size = this.size;
        GenericFutureListener<? extends Future<?>>[] listeners = this.listeners;
        if (size == listeners.length) {
            this.listeners = listeners = Arrays.copyOf(listeners, size << 1);
        }
        listeners[size] = l;
        this.size = size + 1;
        if (l instanceof GenericProgressiveFutureListener) {
            ++this.progressiveSize;
        }
    }

    public void remove(GenericFutureListener<? extends Future<?>> l) {
        GenericFutureListener<? extends Future<?>>[] listeners = this.listeners;
        int size = this.size;
        for (int i = 0; i < size; ++i) {
            if (listeners[i] != l) continue;
            int listenersToMove = size - i - 1;
            if (listenersToMove > 0) {
                System.arraycopy(listeners, i + 1, listeners, i, listenersToMove);
            }
            listeners[--size] = null;
            this.size = size;
            if (l instanceof GenericProgressiveFutureListener) {
                --this.progressiveSize;
            }
            return;
        }
    }

    public GenericFutureListener<? extends Future<?>>[] listeners() {
        return this.listeners;
    }

    public int size() {
        return this.size;
    }

    public int progressiveSize() {
        return this.progressiveSize;
    }
}

