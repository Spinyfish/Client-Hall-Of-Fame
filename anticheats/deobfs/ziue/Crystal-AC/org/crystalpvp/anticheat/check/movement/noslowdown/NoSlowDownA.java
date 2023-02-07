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

public class NoSlowDownA extends PositionCheck {

    private boolean sameTick;

    public NoSlowDownA(PlayerData playerData) {
        super(playerData, "NoSlowDown (A)");
    }


    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("NoSlowDownA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.isSprinting() && playerData.isSneaking()) {

            if (playerData.getPing() > 350) {
                return;
            }

            if (alert(AlertType.EXPERIMENTAL, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {
                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("NoSlowDownA") && vl >= ConfigurationManager.banVL("NoSlowDownA")) {
                    punish(player);
                }
            }

        } else {
            vl -= 1;
        }
        playerData.setCheckVl(vl, this);
    }
}
