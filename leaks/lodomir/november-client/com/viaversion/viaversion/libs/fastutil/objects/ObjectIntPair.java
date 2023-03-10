/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Pair;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIntImmutablePair;
import java.util.Comparator;

public interface ObjectIntPair<K>
extends Pair<K, Integer> {
    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public ObjectIntPair<K> right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ObjectIntPair<K> right(Integer l) {
        return this.right((int)l);
    }

    default public int secondInt() {
        return this.rightInt();
    }

    @Override
    @Deprecated
    default public Integer second() {
        return this.secondInt();
    }

    default public ObjectIntPair<K> second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectIntPair<K> second(Integer l) {
        return this.second((int)l);
    }

    default public int valueInt() {
        return this.rightInt();
    }

    @Override
    @Deprecated
    default public Integer value() {
        return this.valueInt();
    }

    default public ObjectIntPair<K> value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectIntPair<K> value(Integer l) {
        return this.value((int)l);
    }

    public static <K> ObjectIntPair<K> of(K left, int right) {
        return new ObjectIntImmutablePair<K>(left, right);
    }

    public static <K> Comparator<ObjectIntPair<K>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

