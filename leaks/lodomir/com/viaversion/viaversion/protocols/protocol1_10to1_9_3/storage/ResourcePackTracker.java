/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_10to1_9_3.storage;

import com.viaversion.viaversion.api.connection.StorableObject;

public class ResourcePackTracker
implements StorableObject {
    private String lastHash = "";

    public String getLastHash() {
        return this.lastHash;
    }

    public void setLastHash(String lastHash) {
        this.lastHash = lastHash;
    }

    public String toString() {
        return "ResourcePackTracker{lastHash='" + this.lastHash + '\'' + '}';
    }
}

