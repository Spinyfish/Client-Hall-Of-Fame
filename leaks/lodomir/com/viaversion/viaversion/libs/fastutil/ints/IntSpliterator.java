/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.IntComparator;
import com.viaversion.viaversion.libs.fastutil.ints.IntConsumer;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface IntSpliterator
extends Spliterator.OfInt {
    @Override
    @Deprecated
    default public boolean tryAdvance(Consumer<? super Integer> action) {
        return this.tryAdvance(action instanceof java.util.function.IntConsumer ? (java.util.function.IntConsumer)((Object)action) : action::accept);
    }

    @Override
    default public boolean tryAdvance(IntConsumer action) {
        return this.tryAdvance((java.util.function.IntConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Integer> action) {
        this.forEachRemaining(action instanceof java.util.function.IntConsumer ? (java.util.function.IntConsumer)((Object)action) : action::accept);
    }

    @Override
    default public void forEachRemaining(IntConsumer action) {
        this.forEachRemaining((java.util.function.IntConsumer)action);
    }

    default public long skip(long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        long i = n;
        while (i-- != 0L && this.tryAdvance(unused -> {})) {
        }
        return n - i - 1L;
    }

    @Override
    public IntSpliterator trySplit();

    default public IntComparator getComparator() {
        throw new IllegalStateException();
    }
}

