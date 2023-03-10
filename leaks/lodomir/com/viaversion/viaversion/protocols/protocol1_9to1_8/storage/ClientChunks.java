/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.storage;

import com.google.common.collect.Sets;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.util.Set;

public class ClientChunks
extends StoredObject {
    private final Set<Long> loadedChunks = Sets.newConcurrentHashSet();

    public ClientChunks(UserConnection connection) {
        super(connection);
    }

    public static long toLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw + 0x80000000L;
    }

    public Set<Long> getLoadedChunks() {
        return this.loadedChunks;
    }
}

