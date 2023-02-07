package org.crystalpvp.anticheat.api.update;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@Getter
public class MovementUpdate {

    private Player player;
    private Location to;
    private Location from;
    private PacketPlayInFlying packet;

    public MovementUpdate(final Player player, final Location to, final Location from, final PacketPlayInFlying packet) {
        this.player = player;
        this.to = to;
        this.from = from;
        this.packet = packet;
    }

}