/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCounted
implements ReferenceCounted {
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCounted> refCntUpdater;
    private volatile int refCnt = 1;

    @Override
    public final int refCnt() {
        return this.refCnt;
    }

    protected final void setRefCnt(int refCnt) {
        this.refCnt = refCnt;
    }

    @Override
    public ReferenceCounted retain() {
        int refCnt;
        do {
            if ((refCnt = this.refCnt) == 0) {
                throw new IllegalReferenceCountException(0, 1);
            }
            if (refCnt != Integer.MAX_VALUE) continue;
            throw new IllegalReferenceCountException(Integer.MAX_VALUE, 1);
        } while (!refCntUpdater.compareAndSet(this, refCnt, refCnt + 1));
        return this;
    }

    @Override
    public ReferenceCounted retain(int increment) {
        int refCnt;
        if (increment <= 0) {
            throw new IllegalArgumentException("increment: " + increment + " (expected: > 0)");
        }
        do {
            if ((refCnt = this.refCnt) == 0) {
                throw new IllegalReferenceCountException(0, 1);
            }
            if (refCnt <= Integer.MAX_VALUE - increment) continue;
            throw new IllegalReferenceCountException(refCnt, increment);
        } while (!refCntUpdater.compareAndSet(this, refCnt, refCnt + increment));
        return this;
    }

    @Override
    public final boolean release() {
        int refCnt;
        do {
            if ((refCnt = this.refCnt) != 0) continue;
            throw new IllegalReferenceCountException(0, -1);
        } while (!refCntUpdater.compareAndSet(this, refCnt, refCnt - 1));
        if (refCnt == 1) {
            this.deallocate();
            return true;
        }
        return false;
    }

    @Override
    public final boolean release(int decrement) {
        int refCnt;
        if (decrement <= 0) {
            throw new IllegalArgumentException("decrement: " + decrement + " (expected: > 0)");
        }
        do {
            if ((refCnt = this.refCnt) >= decrement) continue;
            throw new IllegalReferenceCountException(refCnt, -decrement);
        } while (!refCntUpdater.compareAndSet(this, refCnt, refCnt - decrement));
        if (refCnt == decrement) {
            this.deallocate();
            return true;
        }
        return false;
    }

    protected abstract void deallocate();

    static {
        AtomicIntegerFieldUpdater<Object> updater = PlatformDependent.newAtomicIntegerFieldUpdater(AbstractReferenceCounted.class, "refCnt");
        if (updater == null) {
            updater = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCounted.class, "refCnt");
        }
        refCntUpdater = updater;
    }
}

