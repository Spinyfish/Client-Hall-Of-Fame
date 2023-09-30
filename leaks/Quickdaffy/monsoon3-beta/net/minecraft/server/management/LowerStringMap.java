/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.server.management;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LowerStringMap<V>
implements Map<String, V> {
    private final Map<String, V> internalMap = Maps.newLinkedHashMap();

    @Override
    public int size() {
        return this.internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object p_containsKey_1_) {
        return this.internalMap.containsKey(p_containsKey_1_.toString().toLowerCase());
    }

    @Override
    public boolean containsValue(Object p_containsValue_1_) {
        return this.internalMap.containsKey(p_containsValue_1_);
    }

    @Override
    public V get(Object p_get_1_) {
        return this.internalMap.get(p_get_1_.toString().toLowerCase());
    }

    @Override
    public V put(String p_put_1_, V p_put_2_) {
        return this.internalMap.put(p_put_1_.toLowerCase(), p_put_2_);
    }

    @Override
    public V remove(Object p_remove_1_) {
        return this.internalMap.remove(p_remove_1_.toString().toLowerCase());
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> p_putAll_1_) {
        for (Map.Entry<String, V> entry : p_putAll_1_.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.internalMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.internalMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.internalMap.values();
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return this.internalMap.entrySet();
    }
}

