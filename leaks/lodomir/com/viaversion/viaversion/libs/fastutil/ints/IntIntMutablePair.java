/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Pair;
import com.viaversion.viaversion.libs.fastutil.ints.IntIntPair;
import java.io.Serializable;
import java.util.Objects;

public class IntIntMutablePair
implements IntIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected int left;
    protected int right;

    public IntIntMutablePair(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public static IntIntMutablePair of(int left, int right) {
        return new IntIntMutablePair(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public IntIntMutablePair left(int l) {
        this.left = l;
        return this;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    @Override
    public IntIntMutablePair right(int r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntIntPair) {
            return this.left == ((IntIntPair)other).leftInt() && this.right == ((IntIntPair)other).rightInt();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.rightInt() + ">";
    }
}

