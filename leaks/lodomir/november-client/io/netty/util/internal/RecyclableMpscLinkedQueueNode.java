/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.Recycler;
import io.netty.util.internal.MpscLinkedQueueNode;

public abstract class RecyclableMpscLinkedQueueNode<T>
extends MpscLinkedQueueNode<T> {
    private final Recycler.Handle handle;

    protected RecyclableMpscLinkedQueueNode(Recycler.Handle handle) {
        if (handle == null) {
            throw new NullPointerException("handle");
        }
        this.handle = handle;
    }

    @Override
    final void unlink() {
        super.unlink();
        this.recycle(this.handle);
    }

    protected abstract void recycle(Recycler.Handle var1);
}

