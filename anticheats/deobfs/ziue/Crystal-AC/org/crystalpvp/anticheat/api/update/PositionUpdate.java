package org.crystalpvp.anticheat.api.update;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class PositionUpdate extends MovementUpdate {

    public PositionUpdate(Player player, Location to, Location from, PacketPlayInFlying packet) {
        super(player, to, from, packet);
    }

}