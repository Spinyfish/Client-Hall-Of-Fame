/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.collection;

public interface IntObjectMap<V> {
    public V get(int var1);

    public V put(int var1, V var2);

    public void putAll(IntObjectMap<V> var1);

    public V remove(int var1);

    public int size();

    public boolean isEmpty();

    public void clear();

    public boolean containsKey(int var1);

    public boolean containsValue(V var1);

    public Iterable<Entry<V>> entries();

    public int[] keys();

    public V[] values(Class<V> var1);

    public static interface Entry<V> {
        public int key();

        public V value();

        public void setValue(V var1);
    }
}

