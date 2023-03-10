/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.data.entity;

import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StoredEntityImpl
implements StoredEntityData {
    private final Map<Class<?>, Object> storedObjects = new ConcurrentHashMap();
    private final EntityType type;

    public StoredEntityImpl(EntityType type) {
        this.type = type;
    }

    @Override
    public EntityType type() {
        return this.type;
    }

    @Override
    public <T> @Nullable T get(Class<T> objectClass) {
        return (T)this.storedObjects.get(objectClass);
    }

    @Override
    public boolean has(Class<?> objectClass) {
        return this.storedObjects.containsKey(objectClass);
    }

    @Override
    public void put(Object object) {
        this.storedObjects.put(object.getClass(), object);
    }
}

