/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.viaversion.viaversion.util;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.util.Int2IntBiMap;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Int2IntBiHashMap
implements Int2IntBiMap {
    private final Int2IntMap map = new Int2IntOpenHashMap();
    private final Int2IntBiHashMap inverse;

    public Int2IntBiHashMap() {
        this.inverse = new Int2IntBiHashMap(this);
    }

    private Int2IntBiHashMap(Int2IntBiHashMap inverse) {
        this.inverse = inverse;
    }

    @Override
    public Int2IntBiMap inverse() {
        return this.inverse;
    }

    @Override
    public int put(int key, int value) {
        if (this.containsKey(key) && value == this.get(key)) {
            return value;
        }
        Preconditions.checkArgument((!this.containsValue(value) ? 1 : 0) != 0, (String)"value already present: %s", (Object[])new Object[]{value});
        this.map.put(key, value);
        this.inverse.map.put(value, key);
        return this.defaultReturnValue();
    }

    @Override
    public boolean remove(int key, int value) {
        this.map.remove(key, value);
        return this.inverse.map.remove(key, value);
    }

    @Override
    public int get(int key) {
        return this.map.get(key);
    }

    @Override
    public void clear() {
        this.map.clear();
        this.inverse.map.clear();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public void defaultReturnValue(int rv) {
        this.map.defaultReturnValue(rv);
        this.inverse.map.defaultReturnValue(rv);
    }

    @Override
    public int defaultReturnValue() {
        return this.map.defaultReturnValue();
    }

    @Override
    public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
        return this.map.int2IntEntrySet();
    }

    @Override
    public @NonNull IntSet keySet() {
        return this.map.keySet();
    }

    @Override
    public @NonNull IntSet values() {
        return this.inverse.map.keySet();
    }

    @Override
    public boolean containsKey(int key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(int value) {
        return this.inverse.map.containsKey(value);
    }
}

