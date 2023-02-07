package org.crystalpvp.anticheat.api.checks;

import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public abstract class PositionCheck extends AbstractCheck<PositionUpdate> {

    public PositionCheck(PlayerData playerData, String name) {
        super(playerData, PositionUpdate.class, name);
    }

}