/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Index<K, V> {
    private final Map<K, V> keyToValue;
    private final Map<V, K> valueToKey;

    private Index(Map<K, V> keyToValue, Map<V, K> valueToKey) {
        this.keyToValue = keyToValue;
        this.valueToKey = valueToKey;
    }

    @NotNull
    public static <K, V extends Enum<V>> Index<K, V> create(Class<V> type, @NotNull Function<? super V, ? extends K> keyFunction) {
        return Index.create(type, keyFunction, (Enum[])((Enum[])type.getEnumConstants()));
    }

    @SafeVarargs
    @NotNull
    public static <K, V extends Enum<V>> Index<K, V> create(Class<V> type, @NotNull Function<? super V, ? extends K> keyFunction, V ... values) {
        return Index.create(values, (int length) -> new EnumMap(type), keyFunction);
    }

    @SafeVarargs
    @NotNull
    public static <K, V> Index<K, V> create(@NotNull Function<? super V, ? extends K> keyFunction, V ... values) {
        return Index.create(values, HashMap::new, keyFunction);
    }

    @NotNull
    public static <K, V> Index<K, V> create(@NotNull Function<? super V, ? extends K> keyFunction, @NotNull List<V> constants) {
        return Index.create(constants, HashMap::new, keyFunction);
    }

    @NotNull
    private static <K, V> Index<K, V> create(V[] values, IntFunction<Map<V, K>> valueToKeyFactory, @NotNull Function<? super V, ? extends K> keyFunction) {
        return Index.create(Arrays.asList(values), valueToKeyFactory, keyFunction);
    }

    @NotNull
    private static <K, V> Index<K, V> create(List<V> values, IntFunction<Map<V, K>> valueToKeyFactory, @NotNull Function<? super V, ? extends K> keyFunction) {
        int length = values.size();
        HashMap<K, V> keyToValue = new HashMap<K, V>(length);
        Map<K, V> valueToKey = valueToKeyFactory.apply(length);
        for (int i = 0; i < length; ++i) {
            V value = values.get(i);
            K key = keyFunction.apply(value);
            if (keyToValue.putIfAbsent(key, value) != null) {
                throw new IllegalStateException(String.format("Key %s already mapped to value %s", key, keyToValue.get(key)));
            }
            if (valueToKey.putIfAbsent(value, key) == null) continue;
            throw new IllegalStateException(String.format("Value %s already mapped to key %s", value, valueToKey.get(value)));
        }
        return new Index(Collections.unmodifiableMap(keyToValue), Collections.unmodifiableMap(valueToKey));
    }

    @NotNull
    public Set<K> keys() {
        return Collections.unmodifiableSet(this.keyToValue.keySet());
    }

    @Nullable
    public K key(@NotNull V value) {
        return this.valueToKey.get(value);
    }

    @NotNull
    public Set<V> values() {
        return Collections.unmodifiableSet(this.valueToKey.keySet());
    }

    @Nullable
    public V value(@NotNull K key) {
        return this.keyToValue.get(key);
    }
}

