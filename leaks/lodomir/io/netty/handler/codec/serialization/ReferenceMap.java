/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.serialization;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

abstract class ReferenceMap<K, V>
implements Map<K, V> {
    private final Map<K, Reference<V>> delegate;

    protected ReferenceMap(Map<K, Reference<V>> delegate) {
        this.delegate = delegate;
    }

    abstract Reference<V> fold(V var1);

    private V unfold(Reference<V> ref) {
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
        return this.unfold(this.delegate.get(key));
    }

    @Override
    public V put(K key, V value) {
        return this.unfold(this.delegate.put(key, this.fold(value)));
    }

    @Override
    public V remove(Object key) {
        return this.unfold(this.delegate.remove(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<K, V> entry : m.entrySet()) {
            this.delegate.put(entry.getKey(), this.fold(entry.getValue()));
        }
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}

