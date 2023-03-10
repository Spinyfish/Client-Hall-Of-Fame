/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage;

import com.viaversion.viaversion.api.connection.StorableObject;

public class WindowTracker
implements StorableObject {
    private String inventory;
    private int entityId = -1;

    public String getInventory() {
        return this.inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String toString() {
        return "WindowTracker{inventory='" + this.inventory + '\'' + ", entityId=" + this.entityId + '}';
    }
}

