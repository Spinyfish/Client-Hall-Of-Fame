/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.IntBidirectionalIterator;
import java.util.ListIterator;

public interface IntListIterator
extends IntBidirectionalIterator,
ListIterator<Integer> {
    @Override
    default public void set(int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public void set(Integer k) {
        this.set((int)k);
    }

    @Override
    @Deprecated
    default public void add(Integer k) {
        this.add((int)k);
    }

    @Override
    @Deprecated
    default public Integer next() {
        return IntBidirectionalIterator.super.next();
    }

    @Override
    @Deprecated
    default public Integer previous() {
        return IntBidirectionalIterator.super.previous();
    }
}

