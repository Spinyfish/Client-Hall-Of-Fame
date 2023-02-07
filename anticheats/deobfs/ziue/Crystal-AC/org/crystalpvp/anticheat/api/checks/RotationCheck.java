package org.crystalpvp.anticheat.api.checks;

import org.crystalpvp.anticheat.api.update.RotationUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public abstract class RotationCheck extends AbstractCheck<RotationUpdate> {

    public RotationCheck(PlayerData playerData, String name) {

        super(playerData, RotationUpdate.class, name);
    }

}
