package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V> extends RegistryNamespaced<K, V> {
    /**
     * The key of the default value.
     */
    private final K defaultValueKey;

    /**
     * The default value for this registry, retrurned in the place of a null value.
     */
    private V defaultValue;

    public RegistryNamespacedDefaultedByKey(final K p_i46017_1_) {
        this.defaultValueKey = p_i46017_1_;
    }

    public void register(final int id, final K p_177775_2_, final V p_177775_3_) {
        if (this.defaultValueKey.equals(p_177775_2_)) {
            this.defaultValue = p_177775_3_;
        }

        super.register(id, p_177775_2_, p_177775_3_);
    }

    /**
     * validates that this registry's key is non-null
     */
    public void validateKey() {
        Validate.notNull(this.defaultValueKey);
    }

    public V getObject(final K name) {
        final V v = super.getObject(name);
        return v == null ? this.defaultValue : v;
    }

    /**
     * Gets the object identified by the given ID.
     *
     * @param id The id to fetch from the registry
     */
    public V getObjectById(final int id) {
        final V v = super.getObjectById(id);
        return v == null ? this.defaultValue : v;
    }
}
