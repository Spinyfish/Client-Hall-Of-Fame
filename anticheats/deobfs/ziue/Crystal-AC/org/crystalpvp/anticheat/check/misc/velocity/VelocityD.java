package org.crystalpvp.anticheat.check.misc.velocity;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class VelocityD extends PositionCheck {

    public VelocityD(PlayerData playerData) {
        super(playerData, "Velocity (D)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("VelocityD")) {
            return;
        }
        double vl = playerData.getCheckVl(this);


        double offsetX = update.getTo().getX() - update.getFrom().getX();
        if (offsetX < 0) {
            offsetX *= -1;
        }
        double offsetZ = update.getTo().getZ() - update.getFrom().getZ();
        if (offsetZ < 0) {
            offsetZ *= -1;
        }

        final double offsetY = update.getTo().getY() - update.getFrom().getY();

        if (playerData.getVelocityY() > 0.0
                && playerData.isWasOnGround() && !playerData.isUnderBlock()
                && !playerData.isWasUnderBlock() && !playerData.isInLiquid()
                && !playerData.isWasInLiquid() && !playerData.isInWeb()
                && !playerData.isWasInWeb() && !playerData.isOnStairs()
                &&  offsetY > 0.0 && offsetY < 0.42
                && offsetX < 0.45
                && offsetZ < 0.45
                && update.getFrom().getY() % 1.0 == 0.0
                &&!BlockUtil.isBlockingVelocityH(update.getPlayer().getLocation())) {


            final double ratioH = ((offsetX / playerData.getVelocityX()) + (offsetZ / playerData.getVelocityZ())) / 2;

            if (ratioH < 0.10) {

                final int percent = (int) Math.round(ratioH * 100.0);

                if (++vl >= 5) {
                    alert(AlertType.RELEASE, player, String.format("Horizontal %s. VL %.1f/%s.", percent, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                }

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("VelocityD") && vl >= ConfigurationManager.banVL("VelocityD")) {
                    punish(player);
                }
            } else {
                vl -= 0.2;
            }

        }

        playerData.setCheckVl(vl, this);
    }
}
