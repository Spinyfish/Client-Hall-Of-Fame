/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.data.entity;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.ClientEntityIdChangeListener;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.data.entity.StoredEntityImpl;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.flare.fastutil.Int2ObjectSyncMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EntityTrackerBase
implements EntityTracker,
ClientEntityIdChangeListener {
    private final Int2ObjectMap<EntityType> entityTypes = Int2ObjectSyncMap.hashmap();
    private final Int2ObjectMap<StoredEntityData> entityData;
    private final UserConnection connection;
    private final EntityType playerType;
    private int clientEntityId = -1;
    private int currentWorldSectionHeight = 16;
    private int currentMinY;
    private String currentWorld;
    private int biomesSent = -1;

    public EntityTrackerBase(UserConnection connection, @Nullable EntityType playerType) {
        this(connection, playerType, false);
    }

    public EntityTrackerBase(UserConnection connection, @Nullable EntityType playerType, boolean storesEntityData) {
        this.connection = connection;
        this.playerType = playerType;
        this.entityData = storesEntityData ? Int2ObjectSyncMap.hashmap() : null;
    }

    @Override
    public UserConnection user() {
        return this.connection;
    }

    @Override
    public void addEntity(int id, EntityType type) {
        this.entityTypes.put(id, type);
    }

    @Override
    public boolean hasEntity(int id) {
        return this.entityTypes.containsKey(id);
    }

    @Override
    public @Nullable EntityType entityType(int id) {
        return (EntityType)this.entityTypes.get(id);
    }

    @Override
    public @Nullable StoredEntityData entityData(int id) {
        Preconditions.checkArgument((this.entityData != null ? 1 : 0) != 0, (Object)"Entity data storage has to be explicitly enabled via the constructor");
        EntityType type = this.entityType(id);
        return type != null ? this.entityData.computeIfAbsent(id, s -> new StoredEntityImpl(type)) : null;
    }

    @Override
    public @Nullable StoredEntityData entityDataIfPresent(int id) {
        Preconditions.checkArgument((this.entityData != null ? 1 : 0) != 0, (Object)"Entity data storage has to be explicitly enabled via the constructor");
        return (StoredEntityData)this.entityData.get(id);
    }

    @Override
    public void removeEntity(int id) {
        this.entityTypes.remove(id);
        if (this.entityData != null) {
            this.entityData.remove(id);
        }
    }

    @Override
    public void clearEntities() {
        this.entityTypes.clear();
        if (this.entityData != null) {
            this.entityData.clear();
        }
    }

    @Override
    public int clientEntityId() {
        return this.clientEntityId;
    }

    @Override
    public void setClientEntityId(int clientEntityId) {
        StoredEntityData data;
        Preconditions.checkNotNull((Object)this.playerType);
        this.entityTypes.put(clientEntityId, this.playerType);
        if (this.clientEntityId != -1 && this.entityData != null && (data = (StoredEntityData)this.entityData.remove(this.clientEntityId)) != null) {
            this.entityData.put(clientEntityId, data);
        }
        this.clientEntityId = clientEntityId;
    }

    @Override
    public int currentWorldSectionHeight() {
        return this.currentWorldSectionHeight;
    }

    @Override
    public void setCurrentWorldSectionHeight(int currentWorldSectionHeight) {
        this.currentWorldSectionHeight = currentWorldSectionHeight;
    }

    @Override
    public int currentMinY() {
        return this.currentMinY;
    }

    @Override
    public void setCurrentMinY(int currentMinY) {
        this.currentMinY = currentMinY;
    }

    @Override
    public @Nullable String currentWorld() {
        return this.currentWorld;
    }

    @Override
    public void setCurrentWorld(String currentWorld) {
        this.currentWorld = currentWorld;
    }

    @Override
    public int biomesSent() {
        return this.biomesSent;
    }

    @Override
    public void setBiomesSent(int biomesSent) {
        this.biomesSent = biomesSent;
    }
}

