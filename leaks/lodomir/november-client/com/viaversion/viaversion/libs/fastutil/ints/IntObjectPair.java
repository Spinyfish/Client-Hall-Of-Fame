/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Pair;
import com.viaversion.viaversion.libs.fastutil.ints.IntObjectImmutablePair;
import java.util.Comparator;

public interface IntObjectPair<V>
extends Pair<Integer, V> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntObjectPair<V> left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntObjectPair<V> left(Integer l) {
        return this.left((int)l);
    }

    default public int firstInt() {
        return this.leftInt();
    }

    @Override
    @Deprecated
    default public Integer first() {
        return this.firstInt();
    }

    default public IntObjectPair<V> first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntObjectPair<V> first(Integer l) {
        return this.first((int)l);
    }

    default public int keyInt() {
        return this.firstInt();
    }

    @Override
    @Deprecated
    default public Integer key() {
        return this.keyInt();
    }

    default public IntObjectPair<V> key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntObjectPair<V> key(Integer l) {
        return this.key((int)l);
    }

    public static <V> IntObjectPair<V> of(int left, V right) {
        return new IntObjectImmutablePair<V>(left, right);
    }

    public static <V> Comparator<IntObjectPair<V>> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return ((Comparable)x.right()).compareTo(y.right());
        };
    }
}

