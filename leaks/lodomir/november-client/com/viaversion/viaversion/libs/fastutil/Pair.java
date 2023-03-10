/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectObjectImmutablePair;
import java.util.Comparator;

public interface Pair<L, R> {
    public L left();

    public R right();

    default public Pair<L, R> left(L l) {
        throw new UnsupportedOperationException();
    }

    default public Pair<L, R> right(R r) {
        throw new UnsupportedOperationException();
    }

    default public L first() {
        return this.left();
    }

    default public R second() {
        return this.right();
    }

    default public Pair<L, R> first(L l) {
        return this.left(l);
    }

    default public Pair<L, R> second(R r) {
        return this.right(r);
    }

    default public Pair<L, R> key(L l) {
        return this.left(l);
    }

    default public Pair<L, R> value(R r) {
        return this.right(r);
    }

    default public L key() {
        return this.left();
    }

    default public R value() {
        return this.right();
    }

    public static <L, R> Pair<L, R> of(L l, R r) {
        return new ObjectObjectImmutablePair<L, R>(l, r);
    }

    public static <L, R> Comparator<Pair<L, R>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return ((Comparable)x.right()).compareTo(y.right());
        };
    }
}

