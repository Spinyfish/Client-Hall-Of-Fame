/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;

public final class InventoryAcknowledgements
implements StorableObject {
    private final IntSet ids = new IntOpenHashSet();

    public void addId(int id) {
        this.ids.add(id);
    }

    public boolean removeId(int id) {
        return this.ids.remove(id);
    }
}

