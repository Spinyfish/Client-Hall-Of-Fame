/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.IntComparators;
import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface IntComparator
extends Comparator<Integer> {
    @Override
    public int compare(int var1, int var2);

    default public IntComparator reversed() {
        return IntComparators.oppositeComparator(this);
    }

    @Override
    @Deprecated
    default public int compare(Integer ok1, Integer ok2) {
        return this.compare((int)ok1, (int)ok2);
    }

    default public IntComparator thenComparing(IntComparator second) {
        return (IntComparator & Serializable)(k1, k2) -> {
            int comp = this.compare(k1, k2);
            return comp == 0 ? second.compare(k1, k2) : comp;
        };
    }

    @Override
    default public Comparator<Integer> thenComparing(Comparator<? super Integer> second) {
        if (second instanceof IntComparator) {
            return this.thenComparing((IntComparator)second);
        }
        return Comparator.super.thenComparing(second);
    }
}

