/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface IntUnaryOperator
extends UnaryOperator<Integer>,
java.util.function.IntUnaryOperator {
    @Override
    public int apply(int var1);

    public static IntUnaryOperator identity() {
        return i -> i;
    }

    public static IntUnaryOperator negation() {
        return i -> -i;
    }

    @Override
    @Deprecated
    default public int applyAsInt(int x) {
        return this.apply(x);
    }

    @Override
    @Deprecated
    default public Integer apply(Integer x) {
        return this.apply((int)x);
    }
}

