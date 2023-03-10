/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.SortedPair;
import com.viaversion.viaversion.libs.fastutil.ints.IntIntImmutableSortedPair;
import com.viaversion.viaversion.libs.fastutil.ints.IntIntPair;
import java.io.Serializable;

public interface IntIntSortedPair
extends IntIntPair,
SortedPair<Integer>,
Serializable {
    public static IntIntSortedPair of(int left, int right) {
        return IntIntImmutableSortedPair.of(left, right);
    }

    default public boolean contains(int e) {
        return e == this.leftInt() || e == this.rightInt();
    }

    @Override
    @Deprecated
    default public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.contains((Integer)o);
    }
}

