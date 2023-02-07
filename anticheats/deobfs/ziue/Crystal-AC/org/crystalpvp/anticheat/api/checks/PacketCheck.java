package org.crystalpvp.anticheat.api.checks;

import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public abstract class PacketCheck extends AbstractCheck<Packet> {

    public PacketCheck(PlayerData playerData, String name) {

        super(playerData, Packet.class, name);
    }

}