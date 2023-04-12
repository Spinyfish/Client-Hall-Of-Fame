package xyz.unnamed.anticheat.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.unnamed.anticheat.model.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@RequiredArgsConstructor
public class PlayerDataService {
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public PlayerData registerPlayer(Player player) {
        PlayerData playerData = new PlayerData(player);
        dataMap.put(player.getUniqueId(), playerData);
        return playerData;
    }

    public PlayerData getPlayer(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    public void unregisterPlayer(Player player) {
        dataMap.remove(player.getUniqueId());
    }
}
