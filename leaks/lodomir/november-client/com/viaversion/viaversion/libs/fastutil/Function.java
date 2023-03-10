/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil;

@FunctionalInterface
public interface Function<K, V>
extends java.util.function.Function<K, V> {
    @Override
    default public V apply(K key) {
        return this.get(key);
    }

    default public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(Object var1);

    default public V getOrDefault(Object key, V defaultValue) {
        V value = this.get(key);
        return value != null || this.containsKey(key) ? value : defaultValue;
    }

    default public boolean containsKey(Object key) {
        return true;
    }

    default public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    default public int size() {
        return -1;
    }

    default public void clear() {
        throw new UnsupportedOperationException();
    }
}

