/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.function.BinaryOperator;

@FunctionalInterface
public interface IntBinaryOperator
extends BinaryOperator<Integer>,
java.util.function.IntBinaryOperator {
    @Override
    public int apply(int var1, int var2);

    @Override
    @Deprecated
    default public int applyAsInt(int x, int y) {
        return this.apply(x, y);
    }

    @Override
    @Deprecated
    default public Integer apply(Integer x, Integer y) {
        return this.apply((int)x, (int)y);
    }
}

