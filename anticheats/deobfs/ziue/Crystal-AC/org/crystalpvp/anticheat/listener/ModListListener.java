package org.crystalpvp.anticheat.listener;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.event.ModListRetrieveEvent;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class ModListListener implements Listener {
    
    @EventHandler
    public void onModListRetrieve(ModListRetrieveEvent event) {

        final Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);

        if (playerData == null) {
            return;
        }

        final Map<String, String> mods = event.getMods();

        CrystalAC.getInstance().getClientManager().onModList(playerData, player, mods);
    }

}
