package org.crystalpvp.anticheat.check.misc.velocity;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class VelocityA extends PositionCheck {

    public VelocityA(PlayerData playerData) {
        super(playerData, "Velocity (A)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("VelocityA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.getVelocityY() > 0.0 && !playerData.isUnderBlock() && !playerData
                .isWasUnderBlock() && !playerData.isInLiquid() && !playerData.isWasInLiquid() && !this
                .playerData.isInWeb() && !playerData.isWasInWeb() && System.currentTimeMillis() - this
                .playerData.getLastDelayedMovePacket() > 220L && System.currentTimeMillis() - playerData
                .getLastMovePacket().getTimestamp() < 110L) {
            final int threshold = 10 + MathUtil.pingFormula(playerData.getPing()) * 2;

            if (++vl >= threshold) {
                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("VelocityA") && vl >= ConfigurationManager.banVL("VelocityA")) {
                    punish(player);
                }

                playerData.setVelocityY(0.0);

                vl = 0;
            }
        } else {
            vl = 0;
        }

        playerData.setCheckVl(vl, this);
    }

}
