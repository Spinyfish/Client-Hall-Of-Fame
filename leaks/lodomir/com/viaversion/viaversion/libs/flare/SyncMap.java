/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.libs.flare;

import com.viaversion.viaversion.libs.flare.SyncMapImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface SyncMap<K, V>
extends ConcurrentMap<K, V> {
    public static <K, V> @NonNull SyncMap<K, V> hashmap() {
        return SyncMap.of(HashMap::new, 16);
    }

    public static <K, V> @NonNull SyncMap<K, V> hashmap(int initialCapacity) {
        return SyncMap.of(HashMap::new, initialCapacity);
    }

    public static <K> @NonNull Set<K> hashset() {
        return SyncMap.setOf(HashMap::new, 16);
    }

    public static <K> @NonNull Set<K> hashset(int initialCapacity) {
        return SyncMap.setOf(HashMap::new, initialCapacity);
    }

    public static <K, V> @NonNull SyncMap<K, V> of(@NonNull IntFunction<Map<K, ExpungingEntry<V>>> function, int initialCapacity) {
        return new SyncMapImpl<K, V>(function, initialCapacity);
    }

    public static <K> @NonNull Set<K> setOf(@NonNull IntFunction<Map<K, ExpungingEntry<Boolean>>> function, int initialCapacity) {
        return Collections.newSetFromMap(new SyncMapImpl(function, initialCapacity));
    }

    @Override
    public @NonNull Set<Map.Entry<K, V>> entrySet();

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

        public <K> @NonNull InsertionResult<V> computeIfAbsent(@Nullable K var1, @NonNull Function<? super K, ? extends V> var2);

        public <K> @NonNull InsertionResult<V> computeIfPresent(@Nullable K var1, @NonNull BiFunction<? super K, ? super V, ? extends V> var2);

        public <K> @NonNull InsertionResult<V> compute(@Nullable K var1, @NonNull BiFunction<? super K, ? super V, ? extends V> var2);

        public void set(@NonNull V var1);

        public boolean replace(@NonNull Object var1, @Nullable V var2);

        public @Nullable V clear();

        public boolean trySet(@NonNull V var1);

        public @Nullable V tryReplace(@NonNull V var1);

        public boolean tryExpunge();

        public boolean tryUnexpungeAndSet(@NonNull V var1);

        public <K> boolean tryUnexpungeAndCompute(@Nullable K var1, @NonNull Function<? super K, ? extends V> var2);

        public <K> boolean tryUnexpungeAndCompute(@Nullable K var1, @NonNull BiFunction<? super K, ? super V, ? extends V> var2);
    }
}

