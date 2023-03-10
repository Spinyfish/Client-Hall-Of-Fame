/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntComparator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSet;
import java.util.Map;
import java.util.SortedMap;

public interface Int2ObjectSortedMap<V>
extends Int2ObjectMap<V>,
SortedMap<Integer, V> {
    public Int2ObjectSortedMap<V> subMap(int var1, int var2);

    public Int2ObjectSortedMap<V> headMap(int var1);

    public Int2ObjectSortedMap<V> tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2ObjectSortedMap<V> subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2ObjectSortedMap<V> headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2ObjectSortedMap<V> tailMap(Integer from) {
        return this.tailMap((int)from);
    }

    @Override
    @Deprecated
    default public Integer firstKey() {
        return this.firstIntKey();
    }

    @Override
    @Deprecated
    default public Integer lastKey() {
        return this.lastIntKey();
    }

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<Integer, V>> entrySet() {
        return this.int2ObjectEntrySet();
    }

    @Override
    public ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public ObjectCollection<V> values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Int2ObjectMap.Entry<V>>,
    Int2ObjectMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap.Entry<V> var1);
    }
}

