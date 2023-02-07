package org.crystalpvp.anticheat.check.movement.noslowdown;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class NoSlowDownB extends PositionCheck {

    private boolean sameTick;

    public NoSlowDownB(PlayerData playerData) {
        super(playerData, "NoSlowDown (B)");
    }


    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("NoSlowDownB") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.getPing() > 350) {
            return;
        }

        if (playerData.isSprinting() && player.isBlocking()) {

            if (++vl > 10) {
                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("NoSlowDownB") && vl >= ConfigurationManager.banVL("NoSlowDownB")) {
                    punish(player);
                }
            }

        } else {
            vl -= 0.25;
        }
        playerData.setCheckVl(vl, this);
    }
}