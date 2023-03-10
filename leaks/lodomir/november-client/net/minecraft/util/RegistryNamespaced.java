/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 */
package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistrySimple;

public class RegistryNamespaced<K, V>
extends RegistrySimple<K, V>
implements IObjectIntIterable<V> {
    protected final ObjectIntIdentityMap underlyingIntegerMap = new ObjectIntIdentityMap();
    protected final Map<V, K> inverseObjectRegistry = ((BiMap)this.registryObjects).inverse();

    public void register(int id, K p_177775_2_, V p_177775_3_) {
        this.underlyingIntegerMap.put(p_177775_3_, id);
        this.putObject(p_177775_2_, p_177775_3_);
    }

    @Override
    protected Map<K, V> createUnderlyingMap() {
        return HashBiMap.create();
    }

    @Override
    public V getObject(K name) {
        return super.getObject(name);
    }

    public K getNameForObject(V p_177774_1_) {
        return this.inverseObjectRegistry.get(p_177774_1_);
    }

    @Override
    public boolean containsKey(K p_148741_1_) {
        return super.containsKey(p_148741_1_);
    }

    public int getIDForObject(V p_148757_1_) {
        return this.underlyingIntegerMap.get(p_148757_1_);
    }

    public V getObjectById(int id) {
        return (V)this.underlyingIntegerMap.getByValue(id);
    }

    @Override
    public Iterator<V> iterator() {
        return this.underlyingIntegerMap.iterator();
    }
}

