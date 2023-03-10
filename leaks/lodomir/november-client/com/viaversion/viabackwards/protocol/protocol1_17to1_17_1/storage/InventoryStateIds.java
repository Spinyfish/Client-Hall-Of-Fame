/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_17to1_17_1.storage;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public final class InventoryStateIds
implements StorableObject {
    private final Int2IntMap ids = new Int2IntOpenHashMap();

    public InventoryStateIds() {
        this.ids.defaultReturnValue(Integer.MAX_VALUE);
    }

    public void setStateId(short containerId, int id) {
        this.ids.put(containerId, id);
    }

    public int removeStateId(short containerId) {
        return this.ids.remove(containerId);
    }
}

