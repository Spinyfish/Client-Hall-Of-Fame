package me.levansj01.verus.compat.v1_7_R4;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WrappedHashMap
extends HashMap {
    private final Map<K, V> internal;

    @Override
    public Collection<V> values() {
        return this.internal.values();
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> biConsumer) {
        this.internal.forEach(biConsumer);
    }

    @Override
    public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        return this.internal.computeIfPresent((Object)k, biFunction);
    }

    @Override
    public Set<K> keySet() {
        return this.internal.keySet();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.internal.entrySet();
    }

    @Override
    public V compute(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        return this.internal.compute((Object)k, biFunction);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> biFunction) {
        this.internal.replaceAll(biFunction);
    }

    @Override
    public V remove(Object object) {
        return this.internal.remove(object);
    }

    @Override
    public int size() {
        return this.internal.size();
    }

    @Override
    public V putIfAbsent(K k, V v) {
        return this.internal.putIfAbsent(k, v);
    }

    @Override
    public V computeIfAbsent(K k, Function<? super K, ? extends V> function) {
        return this.internal.computeIfAbsent((Object)k, function);
    }

    @Override
    public V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> biFunction) {
        return this.internal.merge(k, (Object)v, biFunction);
    }

    @Override
    public void clear() {
        this.internal.clear();
    }

    @Override
    public V get(Object object) {
        return this.internal.get(object);
    }

    @Override
    public V put(K k, V v) {
        return this.internal.put(k, v);
    }

    @Override
    public V getOrDefault(Object object, V v) {
        return this.internal.getOrDefault(object, v);
    }

    public WrappedHashMap(Map<K, V> map) {
        this.internal = map;
    }

    @Override
    public boolean replace(K k, V v, V v2) {
        return this.internal.replace(k, v, v2);
    }

    @Override
    public boolean containsValue(Object object) {
        return this.internal.containsValue(object);
    }

    @Override
    public boolean isEmpty() {
        return this.internal.isEmpty();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        this.internal.putAll(map);
    }

    @Override
    public boolean remove(Object object, Object object2) {
        return this.internal.remove(object, object2);
    }

    @Override
    public V replace(K k, V v) {
        return this.internal.replace(k, v);
    }

    @Override
    public boolean containsKey(Object object) {
        return this.internal.containsKey(object);
    }
}

