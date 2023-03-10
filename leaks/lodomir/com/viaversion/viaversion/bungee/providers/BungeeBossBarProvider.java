/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.bungee.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.bungee.storage.BungeeStorage;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.BossBarProvider;
import java.util.UUID;

public class BungeeBossBarProvider
extends BossBarProvider {
    @Override
    public void handleAdd(UserConnection user, UUID barUUID) {
        BungeeStorage storage;
        if (user.has(BungeeStorage.class) && (storage = user.get(BungeeStorage.class)).getBossbar() != null) {
            storage.getBossbar().add(barUUID);
        }
    }

    @Override
    public void handleRemove(UserConnection user, UUID barUUID) {
        BungeeStorage storage;
        if (user.has(BungeeStorage.class) && (storage = user.get(BungeeStorage.class)).getBossbar() != null) {
            storage.getBossbar().remove(barUUID);
        }
    }
}

