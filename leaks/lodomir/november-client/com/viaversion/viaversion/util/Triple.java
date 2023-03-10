/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.util;

import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    public Triple(@Nullable A first, @Nullable B second, @Nullable C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public @Nullable A first() {
        return this.first;
    }

    public @Nullable B second() {
        return this.second;
    }

    public @Nullable C third() {
        return this.third;
    }

    @Deprecated
    public @Nullable A getFirst() {
        return this.first;
    }

    @Deprecated
    public @Nullable B getSecond() {
        return this.second;
    }

    @Deprecated
    public @Nullable C getThird() {
        return this.third;
    }

    public String toString() {
        return "Triple{" + this.first + ", " + this.second + ", " + this.third + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Triple triple = (Triple)o;
        if (!Objects.equals(this.first, triple.first)) {
            return false;
        }
        if (!Objects.equals(this.second, triple.second)) {
            return false;
        }
        return Objects.equals(this.third, triple.third);
    }

    public int hashCode() {
        int result = this.first != null ? this.first.hashCode() : 0;
        result = 31 * result + (this.second != null ? this.second.hashCode() : 0);
        result = 31 * result + (this.third != null ? this.third.hashCode() : 0);
        return result;
    }
}

