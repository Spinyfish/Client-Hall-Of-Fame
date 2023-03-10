/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.ints.IntCollection;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSet;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

public interface Object2IntSortedMap<K>
extends Object2IntMap<K>,
SortedMap<K, Integer> {
    public Object2IntSortedMap<K> subMap(K var1, K var2);

    public Object2IntSortedMap<K> headMap(K var1);

    public Object2IntSortedMap<K> tailMap(K var1);

    @Override
    @Deprecated
    default public ObjectSortedSet<Map.Entry<K, Integer>> entrySet() {
        return this.object2IntEntrySet();
    }

    @Override
    public ObjectSortedSet<Object2IntMap.Entry<K>> object2IntEntrySet();

    @Override
    public ObjectSortedSet<K> keySet();

    @Override
    public IntCollection values();

    @Override
    public Comparator<? super K> comparator();

    public static interface FastSortedEntrySet<K>
    extends ObjectSortedSet<Object2IntMap.Entry<K>>,
    Object2IntMap.FastEntrySet<K> {
        @Override
        public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> fastIterator();

        public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> fastIterator(Object2IntMap.Entry<K> var1);
    }
}

