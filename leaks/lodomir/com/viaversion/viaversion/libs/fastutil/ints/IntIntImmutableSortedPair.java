/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.SortedPair;
import com.viaversion.viaversion.libs.fastutil.ints.IntIntImmutablePair;
import com.viaversion.viaversion.libs.fastutil.ints.IntIntSortedPair;
import java.io.Serializable;
import java.util.Objects;

public class IntIntImmutableSortedPair
extends IntIntImmutablePair
implements IntIntSortedPair,
Serializable {
    private static final long serialVersionUID = 0L;

    private IntIntImmutableSortedPair(int left, int right) {
        super(left, right);
    }

    public static IntIntImmutableSortedPair of(int left, int right) {
        if (left <= right) {
            return new IntIntImmutableSortedPair(left, right);
        }
        return new IntIntImmutableSortedPair(right, left);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntIntSortedPair) {
            return this.left == ((IntIntSortedPair)other).leftInt() && this.right == ((IntIntSortedPair)other).rightInt();
        }
        if (other instanceof SortedPair) {
            return Objects.equals(this.left, ((SortedPair)other).left()) && Objects.equals(this.right, ((SortedPair)other).right());
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + this.leftInt() + "," + this.rightInt() + "}";
    }
}

