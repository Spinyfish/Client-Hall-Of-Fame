package com.alan.clients.anticheat.listener;

import com.alan.clients.Client;
import com.alan.clients.anticheat.data.PlayerData;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

public final class RegistrationListener {

    public void handleSpawn(final EntityOtherPlayerMP entity) {
        Client.INSTANCE.getCheatDetector().getPlayerMap().put(entity.getUniqueID(), new PlayerData(entity));
    }

    /**
     * Actually destroy the entities as we yknow don't want memory
     * leaks in the client due to a retarded anticheat base lmao.
     */
    public void handleDestroy(final UUID uuid) {
        Client.INSTANCE.getCheatDetector().getPlayerMap().remove(uuid);
    }
}
