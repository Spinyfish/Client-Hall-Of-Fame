/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

public interface Stack<K> {
    public void push(K var1);

    public K pop();

    public boolean isEmpty();

    default public K top() {
        return this.peek(0);
    }

    default public K peek(int i) {
        throw new UnsupportedOperationException();
    }
}

