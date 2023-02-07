package org.crystalpvp.anticheat.api.util;

import net.minecraft.server.v1_8_R3.DedicatedServer;
import org.bukkit.entity.Player;

public class CrystalUtil {

    public static boolean hasPermission(Player player, String perm) {
        return player.hasPermission(perm);
    }



}
