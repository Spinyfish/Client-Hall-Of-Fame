package org.crystalpvp.anticheat.check.misc.velocity;

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

public class VelocityB extends PositionCheck {

    public VelocityB(PlayerData playerData) {
        super(playerData, "Velocity (B)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("VelocityB") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        final double offsetY = update.getTo().getY() - update.getFrom().getY();
        if (playerData.getVelocityY() > 0.0 && playerData.isWasOnGround() && !playerData.isUnderBlock
                () && !playerData.isWasUnderBlock() && !playerData.isInLiquid() && !playerData
                .isWasInLiquid() && !playerData.isInWeb() && !playerData.isWasInWeb() && !playerData
                .isOnStairs() && offsetY > 0.0 && offsetY < 0.42 && update.getFrom().getY() % 1.0 ==
                0.0) {

            final double ratioY = offsetY / playerData.getVelocityY();

            if (ratioY < 0.99 || ratioY > 1.01) {
                final int percent = (int) Math.round(ratioY * 100.0);

                if (++vl >= 5) {
                    alert(AlertType.RELEASE, player, String.format("Vertical %s. VL %.1f/%s.", percent, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                }

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("VelocityB") && vl >= ConfigurationManager.banVL("VelocityB")) {
                    punish(player);
                }
            } else {
                --vl;
            }

            playerData.setCheckVl(vl, this);
        }
    }

}
