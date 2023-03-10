/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.util;

import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Pair<X, Y> {
    private final X key;
    private Y value;

    public Pair(@Nullable X key, @Nullable Y value) {
        this.key = key;
        this.value = value;
    }

    public @Nullable X key() {
        return this.key;
    }

    public @Nullable Y value() {
        return this.value;
    }

    @Deprecated
    public @Nullable X getKey() {
        return this.key;
    }

    @Deprecated
    public @Nullable Y getValue() {
        return this.value;
    }

    @Deprecated
    public void setValue(@Nullable Y value) {
        this.value = value;
    }

    public String toString() {
        return "Pair{" + this.key + ", " + this.value + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Pair pair = (Pair)o;
        if (!Objects.equals(this.key, pair.key)) {
            return false;
        }
        return Objects.equals(this.value, pair.value);
    }

    public int hashCode() {
        int result = this.key != null ? this.key.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }
}

