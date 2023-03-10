/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.MpscLinkedQueueNode;
import io.netty.util.internal.MpscLinkedQueuePad1;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class MpscLinkedQueueTailRef<E>
extends MpscLinkedQueuePad1<E> {
    private static final long serialVersionUID = 8717072462993327429L;
    private static final AtomicReferenceFieldUpdater<MpscLinkedQueueTailRef, MpscLinkedQueueNode> UPDATER;
    private volatile transient MpscLinkedQueueNode<E> tailRef;

    MpscLinkedQueueTailRef() {
    }

    protected final MpscLinkedQueueNode<E> tailRef() {
        return this.tailRef;
    }

    protected final void setTailRef(MpscLinkedQueueNode<E> tailRef) {
        this.tailRef = tailRef;
    }

    protected final MpscLinkedQueueNode<E> getAndSetTailRef(MpscLinkedQueueNode<E> tailRef) {
        return UPDATER.getAndSet(this, tailRef);
    }

    static {
        AtomicReferenceFieldUpdater<MpscLinkedQueueTailRef, Object> updater = PlatformDependent.newAtomicReferenceFieldUpdater(MpscLinkedQueueTailRef.class, "tailRef");
        if (updater == null) {
            updater = AtomicReferenceFieldUpdater.newUpdater(MpscLinkedQueueTailRef.class, MpscLinkedQueueNode.class, "tailRef");
        }
        UPDATER = updater;
    }
}

