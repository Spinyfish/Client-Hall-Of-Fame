package org.crystalpvp.anticheat.check.movement.speed;

import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class SpeedA extends PositionCheck {

    private double lastDist;
    private boolean lastOnGround;
    double vl = playerData.getCheckVl(this);

    public SpeedA(PlayerData playerData) { super(playerData, "Speed (A - Disable Check)"); }

    @Override
    @SuppressWarnings("deprecation")
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("SpeedA")) {
            return;
        }

        double distX = update.getTo().getX() - update.getFrom().getX();
        double distZ = update.getTo().getZ() - update.getFrom().getZ();
        double dist = (distX * distX) + (distZ * distZ);
        double lastDist = this.lastDist;
        this.lastDist = dist;

        boolean onGround = update.getPlayer().isOnGround();
        boolean lastOnGround = this.lastOnGround;
        this.lastOnGround = onGround;

        float friction = 1.08F;
        double shiftedLastDist = lastDist * friction;
        double equalness = dist - shiftedLastDist;
        double scaledEqualness = equalness * 158;

        if(!onGround && !lastOnGround && player.isOnGround()) {
            if (scaledEqualness >= 1.0) {
                if (alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("SpeedA") && vl >= ConfigurationManager.banVL("SpeedA")) {
                        punish(player);
                    }
                }
            }
        }
        playerData.setCheckVl(vl, this);
    }
}
