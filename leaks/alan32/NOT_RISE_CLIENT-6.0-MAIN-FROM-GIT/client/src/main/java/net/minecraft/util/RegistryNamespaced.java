package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced<K, V> extends RegistrySimple<K, V> implements IObjectIntIterable<V> {
    protected final ObjectIntIdentityMap<V> underlyingIntegerMap = new ObjectIntIdentityMap();
    protected final Map<V, K> inverseObjectRegistry;

    public RegistryNamespaced() {
        this.inverseObjectRegistry = ((BiMap) this.registryObjects).inverse();
    }

    public void register(final int id, final K p_177775_2_, final V p_177775_3_) {
        this.underlyingIntegerMap.put(p_177775_3_, id);
        this.putObject(p_177775_2_, p_177775_3_);
    }

    protected Map<K, V> createUnderlyingMap() {
        return HashBiMap.create();
    }

    public V getObject(final K name) {
        return super.getObject(name);
    }

    /**
     * Gets the name we use to identify the given object.
     */
    public K getNameForObject(final V p_177774_1_) {
        return this.inverseObjectRegistry.get(p_177774_1_);
    }

    /**
     * Does this registry contain an entry for the given key?
     */
    public boolean containsKey(final K p_148741_1_) {
        return super.containsKey(p_148741_1_);
    }

    /**
     * Gets the integer ID we use to identify the given object.
     */
    public int getIDForObject(final V p_148757_1_) {
        return this.underlyingIntegerMap.get(p_148757_1_);
    }

    /**
     * Gets the object identified by the given ID.
     *
     * @param id The id to fetch from the registry
     */
    public V getObjectById(final int id) {
        return this.underlyingIntegerMap.getByValue(id);
    }

    public Iterator<V> iterator() {
        return this.underlyingIntegerMap.iterator();
    }
}
