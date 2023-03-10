/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Pair;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIntPair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectIntImmutablePair<K>
implements ObjectIntPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final int right;

    public ObjectIntImmutablePair(K left, int right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectIntImmutablePair<K> of(K left, int right) {
        return new ObjectIntImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ObjectIntPair) {
            return Objects.equals(this.left, ((ObjectIntPair)other).left()) && this.right == ((ObjectIntPair)other).rightInt();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightInt() + ">";
    }
}

