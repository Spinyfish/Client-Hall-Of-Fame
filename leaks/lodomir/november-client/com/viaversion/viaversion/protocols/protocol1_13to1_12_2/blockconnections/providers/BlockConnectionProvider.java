/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;

public class BlockConnectionProvider
implements Provider {
    public int getBlockData(UserConnection connection, int x, int y, int z) {
        int oldId = this.getWorldBlockData(connection, x, y, z);
        return Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId);
    }

    public int getWorldBlockData(UserConnection connection, int x, int y, int z) {
        return -1;
    }

    public void storeBlock(UserConnection connection, int x, int y, int z, int blockState) {
    }

    public void removeBlock(UserConnection connection, int x, int y, int z) {
    }

    public void clearStorage(UserConnection connection) {
    }

    public void unloadChunk(UserConnection connection, int x, int z) {
    }

    public boolean storesBlocks() {
        return false;
    }
}

