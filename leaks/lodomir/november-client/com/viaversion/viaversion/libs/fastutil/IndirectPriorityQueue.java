/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

import java.util.Comparator;

public interface IndirectPriorityQueue<K> {
    public void enqueue(int var1);

    public int dequeue();

    default public boolean isEmpty() {
        return this.size() == 0;
    }

    public int size();

    public void clear();

    public int first();

    default public int last() {
        throw new UnsupportedOperationException();
    }

    default public void changed() {
        this.changed(this.first());
    }

    public Comparator<? super K> comparator();

    default public void changed(int index) {
        throw new UnsupportedOperationException();
    }

    default public void allChanged() {
        throw new UnsupportedOperationException();
    }

    default public boolean contains(int index) {
        throw new UnsupportedOperationException();
    }

    default public boolean remove(int index) {
        throw new UnsupportedOperationException();
    }

    default public int front(int[] a) {
        throw new UnsupportedOperationException();
    }
}

