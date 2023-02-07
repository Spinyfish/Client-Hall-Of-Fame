package org.crystalpvp.anticheat.api.checks;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.event.Event;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public abstract class EventCheck extends AbstractCheck<Event> {

    public EventCheck(PlayerData playerData, String name) {

        super(playerData, Event.class, name);
    }

}
