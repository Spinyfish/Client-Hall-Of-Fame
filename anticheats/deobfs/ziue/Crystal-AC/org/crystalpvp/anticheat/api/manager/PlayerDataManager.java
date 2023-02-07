package org.crystalpvp.anticheat.api.manager;

import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    
    public void addPlayerData(Player player) {
        this.playerDataMap.put(player.getUniqueId(), new PlayerData());
    }
    
    public void removePlayerData(Player player) {
        this.playerDataMap.remove(player.getUniqueId());
    }

    public boolean hasPlayerData(Player player) {
        return this.playerDataMap.containsKey(player.getUniqueId());
    }

    public PlayerData getPlayerData(Player player) {
        return this.playerDataMap.get(player.getUniqueId());
    }

}
