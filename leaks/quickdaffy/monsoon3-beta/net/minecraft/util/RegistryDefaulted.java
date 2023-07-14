/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.RegistrySimple;

public class RegistryDefaulted<K, V>
extends RegistrySimple<K, V> {
    private final V defaultObject;

    public RegistryDefaulted(V defaultObjectIn) {
        this.defaultObject = defaultObjectIn;
    }

    @Override
    public V getObject(K name) {
        Object v = super.getObject(name);
        return v == null ? this.defaultObject : v;
    }
}

