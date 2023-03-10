/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;

public abstract class ConnectionHandler {
    public abstract int connect(UserConnection var1, Position var2, int var3);

    public int getBlockData(UserConnection user, Position position) {
        return Via.getManager().getProviders().get(BlockConnectionProvider.class).getBlockData(user, position.x(), position.y(), position.z());
    }
}

