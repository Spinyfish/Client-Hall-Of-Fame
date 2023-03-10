/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Pair;
import com.viaversion.viaversion.libs.fastutil.ints.IntIntImmutablePair;
import java.util.Comparator;

public interface IntIntPair
extends Pair<Integer, Integer> {
    public int leftInt();

    @Override
    @Deprecated
    default public Integer left() {
        return this.leftInt();
    }

    default public IntIntPair left(int l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntIntPair left(Integer l) {
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

    default public IntIntPair first(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntIntPair first(Integer l) {
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

    default public IntIntPair key(int l) {
        return this.left(l);
    }

    @Deprecated
    default public IntIntPair key(Integer l) {
        return this.key((int)l);
    }

    public int rightInt();

    @Override
    @Deprecated
    default public Integer right() {
        return this.rightInt();
    }

    default public IntIntPair right(int r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public IntIntPair right(Integer l) {
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

    default public IntIntPair second(int r) {
        return this.right(r);
    }

    @Deprecated
    default public IntIntPair second(Integer l) {
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

    default public IntIntPair value(int r) {
        return this.right(r);
    }

    @Deprecated
    default public IntIntPair value(Integer l) {
        return this.value((int)l);
    }

    public static IntIntPair of(int left, int right) {
        return new IntIntImmutablePair(left, right);
    }

    public static Comparator<IntIntPair> lexComparator() {
        return (x, y) -> {
            int t = Integer.compare(x.leftInt(), y.leftInt());
            if (t != 0) {
                return t;
            }
            return Integer.compare(x.rightInt(), y.rightInt());
        };
    }
}

