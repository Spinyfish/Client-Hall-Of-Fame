/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

import java.util.Comparator;

public interface PriorityQueue<K> {
    public void enqueue(K var1);

    public K dequeue();

    default public boolean isEmpty() {
        return this.size() == 0;
    }

    public int size();

    public void clear();

    public K first();

    default public K last() {
        throw new UnsupportedOperationException();
    }

    default public void changed() {
        throw new UnsupportedOperationException();
    }

    public Comparator<? super K> comparator();
}

