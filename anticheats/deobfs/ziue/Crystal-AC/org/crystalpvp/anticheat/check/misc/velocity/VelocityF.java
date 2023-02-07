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

public class VelocityF extends PositionCheck {

    private double lastY;

    public VelocityF(PlayerData playerData) {
        super(playerData, "Velocity (F)");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("VelocityF")) {
            return;
        }
        double offsetY = update.getTo().getY() - update.getFrom().getY();

        if (player.getMaximumNoDamageTicks() == 20 && this.playerData.getVelocityY() > 0.0D && this.playerData.isOnGround() &&
                (update.getFrom().getY() % 1.0D) == 0.0D && !this.playerData.isUnderBlock() &&
                !this.playerData.isInLiquid() && offsetY > 0.0D && offsetY < 0.41999998688697815D) {

            long time = System.currentTimeMillis() - this.playerData.getLastVelocity();
            long difference = this.playerData.getPing() - time;
            double vl = this.getVl();

            if (difference > 10L) {
                if (++vl >= 5) {
                    alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                }
                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("VelocityF") && vl >= ConfigurationManager.banVL("VelocityF")) {
                    punish(player);
                }

            } else {
                vl = 0;
            }

            playerData.setCheckVl(vl, this);
        }
    }
}
