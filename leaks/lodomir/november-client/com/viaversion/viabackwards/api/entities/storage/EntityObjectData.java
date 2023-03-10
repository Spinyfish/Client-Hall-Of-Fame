/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.api.entities.storage;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.entities.storage.EntityData;

public class EntityObjectData
extends EntityData {
    private final int objectData;

    public EntityObjectData(BackwardsProtocol<?, ?, ?, ?> protocol, String key, int id, int replacementId, int objectData) {
        super(protocol, key, id, replacementId);
        this.objectData = objectData;
    }

    @Override
    public boolean isObjectType() {
        return true;
    }

    @Override
    public int objectData() {
        return this.objectData;
    }
}

