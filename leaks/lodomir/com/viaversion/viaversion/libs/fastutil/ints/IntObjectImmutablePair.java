/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Pair;
import com.viaversion.viaversion.libs.fastutil.ints.IntObjectPair;
import java.io.Serializable;
import java.util.Objects;

public class IntObjectImmutablePair<V>
implements IntObjectPair<V>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final int left;
    protected final V right;

    public IntObjectImmutablePair(int left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <V> IntObjectImmutablePair<V> of(int left, V right) {
        return new IntObjectImmutablePair<V>(left, right);
    }

    @Override
    public int leftInt() {
        return this.left;
    }

    @Override
    public V right() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntObjectPair) {
            return this.left == ((IntObjectPair)other).leftInt() && Objects.equals(this.right, ((IntObjectPair)other).right());
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right == null ? 0 : this.right.hashCode());
    }

    public String toString() {
        return "<" + this.leftInt() + "," + this.right() + ">";
    }
}

