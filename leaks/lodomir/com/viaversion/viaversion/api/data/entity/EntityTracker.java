/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.data.entity;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface EntityTracker {
    public UserConnection user();

    public void addEntity(int var1, EntityType var2);

    public boolean hasEntity(int var1);

    public @Nullable EntityType entityType(int var1);

    public void removeEntity(int var1);

    public void clearEntities();

    public @Nullable StoredEntityData entityData(int var1);

    public @Nullable StoredEntityData entityDataIfPresent(int var1);

    public int clientEntityId();

    public void setClientEntityId(int var1);

    public int currentWorldSectionHeight();

    public void setCurrentWorldSectionHeight(int var1);

    public int currentMinY();

    public void setCurrentMinY(int var1);

    public @Nullable String currentWorld();

    public void setCurrentWorld(String var1);

    public int biomesSent();

    public void setBiomesSent(int var1);
}

