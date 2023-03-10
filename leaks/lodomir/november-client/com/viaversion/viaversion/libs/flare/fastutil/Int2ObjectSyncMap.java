/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.libs.flare.fastutil;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.libs.flare.fastutil.Int2ObjectSyncMapImpl;
import com.viaversion.viaversion.libs.flare.fastutil.Int2ObjectSyncMapSet;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Int2ObjectSyncMap<V>
extends Int2ObjectMap<V> {
    public static <V> @NonNull Int2ObjectSyncMap<V> hashmap() {
        return Int2ObjectSyncMap.of(Int2ObjectOpenHashMap::new, 16);
    }

    public static <V> @NonNull Int2ObjectSyncMap<V> hashmap(int initialCapacity) {
        return Int2ObjectSyncMap.of(Int2ObjectOpenHashMap::new, initialCapacity);
    }

    public static @NonNull IntSet hashset() {
        return Int2ObjectSyncMap.setOf(Int2ObjectOpenHashMap::new, 16);
    }

    public static @NonNull IntSet hashset(int initialCapacity) {
        return Int2ObjectSyncMap.setOf(Int2ObjectOpenHashMap::new, initialCapacity);
    }

    public static <V> @NonNull Int2ObjectSyncMap<V> of(@NonNull IntFunction<Int2ObjectMap<ExpungingEntry<V>>> function, int initialCapacity) {
        return new Int2ObjectSyncMapImpl<V>(function, initialCapacity);
    }

    public static @NonNull IntSet setOf(@NonNull IntFunction<Int2ObjectMap<ExpungingEntry<Boolean>>> function, int initialCapacity) {
        return new Int2ObjectSyncMapSet(new Int2ObjectSyncMapImpl<Boolean>(function, initialCapacity));
    }

    @Override
    public @NonNull ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet();

    @Override
    public int size();

    @Override
    public void clear();

    public static interface InsertionResult<V> {
        public byte operation();

        public @Nullable V previous();

        public @Nullable V current();
    }

    public static interface ExpungingEntry<V> {
        public boolean exists();

        public @Nullable V get();

        public @NonNull V getOr(@NonNull V var1);

        public @NonNull InsertionResult<V> setIfAbsent(@NonNull V var1);

        public @NonNull InsertionResult<V> computeIfAbsent(int var1, @NonNull IntFunction<? extends V> var2);

        public @NonNull InsertionResult<V> computeIfAbsentPrimitive(int var1, @NonNull Int2ObjectFunction<? extends V> var2);

        public @NonNull InsertionResult<V> computeIfPresent(int var1, @NonNull BiFunction<? super Integer, ? super V, ? extends V> var2);

        public @NonNull InsertionResult<V> compute(int var1, @NonNull BiFunction<? super Integer, ? super V, ? extends V> var2);

        public void set(@NonNull V var1);

        public boolean replace(@NonNull Object var1, @Nullable V var2);

        public @Nullable V clear();

        public boolean trySet(@NonNull V var1);

        public @Nullable V tryReplace(@NonNull V var1);

        public boolean tryExpunge();

        public boolean tryUnexpungeAndSet(@NonNull V var1);

        public boolean tryUnexpungeAndCompute(int var1, @NonNull IntFunction<? extends V> var2);

        public boolean tryUnexpungeAndComputePrimitive(int var1, @NonNull Int2ObjectFunction<? extends V> var2);

        public boolean tryUnexpungeAndCompute(int var1, @NonNull BiFunction<? super Integer, ? super V, ? extends V> var2);
    }
}

