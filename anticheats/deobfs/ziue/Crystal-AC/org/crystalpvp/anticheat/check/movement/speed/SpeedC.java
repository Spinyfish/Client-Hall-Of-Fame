package org.crystalpvp.anticheat.check.movement.speed;

import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class SpeedC extends PositionCheck {

    private int threshold;
    private Double lastAngle;

    public SpeedC(PlayerData playerData) {
        super(playerData, "OmniSprint (C)");
    }

    @Override
    public void handleCheck(Player player, PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("SpeedC")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.isSprinting()) {
            final double angle = Math.toDegrees(-Math.atan2(update.getTo().getX() - update.getFrom().getX(), update.getTo().getZ() - update.getFrom().getZ()));
            final double angleDiff = Math.min(MathUtil.getDistanceBetweenAngles360(angle, update.getTo().getYaw()), MathUtil.getDistanceBetweenAngles360(angle, update.getFrom().getYaw()));
            if (this.lastAngle != null) {
                final double lastAngleDiff = MathUtil.getDistanceBetweenAngles360(this.lastAngle, angleDiff);
                if (angleDiff > 47.5) {
                    if (lastAngleDiff < 5.0 && this.threshold++ > 4) {
                        alert(AlertType.RELEASE, player, String.format("P %.1f. VL %.1f/%s.", lastAngleDiff, angle, Math.round(threshold), ++vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("SpeedC") && vl >= ConfigurationManager.banVL("SpeedC")) {
                            punish(player);
                        }
                    }
                }
                else {
                    this.threshold = 0;
                }
            }
            this.lastAngle = angleDiff;
        } else {
            this.lastAngle = null;
            this.threshold = 0;
        }
        playerData.setCheckVl(vl, this);
    }
}
