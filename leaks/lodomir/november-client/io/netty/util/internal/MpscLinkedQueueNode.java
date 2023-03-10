/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class MpscLinkedQueueNode<T> {
    private static final AtomicReferenceFieldUpdater<MpscLinkedQueueNode, MpscLinkedQueueNode> nextUpdater;
    private volatile MpscLinkedQueueNode<T> next;

    final MpscLinkedQueueNode<T> next() {
        return this.next;
    }

    final void setNext(MpscLinkedQueueNode<T> newNext) {
        nextUpdater.lazySet(this, newNext);
    }

    public abstract T value();

    protected T clearMaybe() {
        return this.value();
    }

    void unlink() {
        this.setNext(null);
    }

    static {
        AtomicReferenceFieldUpdater<MpscLinkedQueueNode, Object> u = PlatformDependent.newAtomicReferenceFieldUpdater(MpscLinkedQueueNode.class, "next");
        if (u == null) {
            u = AtomicReferenceFieldUpdater.newUpdater(MpscLinkedQueueNode.class, MpscLinkedQueueNode.class, "next");
        }
        nextUpdater = u;
    }
}

