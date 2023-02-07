package org.crystalpvp.anticheat.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FlyI extends PositionCheck {

    private int illegalMovements;
    private int legalMovements;

    public FlyI(PlayerData playerData) {
        super(playerData, "Flight (I)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("FlyI")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.getVelocityH() == 0) {
            final double offsetH = Math.hypot(update.getTo().getX() - update.getFrom().getX(), update.getTo().getZ() - update.getFrom().getZ());

            int speed = 0;

            for (final PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    speed = effect.getAmplifier() + 1;
                    break;
                }
            }

            double limit;

            if (this.playerData.isOnGround()) {
                limit = 0.34;

                if (this.playerData.isOnStairs()) {
                    limit = 0.45;
                }
                else if (this.playerData.isOnIce()) {
                    if (this.playerData.isUnderBlock()) {
                        limit = 1.3;
                    }
                    else {
                        limit = 0.65;
                    }
                }
                else if (this.playerData.isUnderBlock()) {
                    limit = 0.7;
                }
                limit += ((player.getWalkSpeed() > 0.2f) ? (player.getWalkSpeed() * 10.0f * 0.33f) : 0.0f);
                limit += 0.06 * speed;
            }
            else {
                limit = 0.36;
                if (this.playerData.isOnStairs()) {
                    limit = 0.45;
                }
                else if (this.playerData.isOnIce()) {
                    if (this.playerData.isUnderBlock()) {
                        limit = 1.3;
                    }
                    else {
                        limit = 0.65;
                    }
                }
                else if (this.playerData.isUnderBlock()) {
                    limit = 0.7;
                }
                limit += ((player.getWalkSpeed() > 0.2f) ? (player.getWalkSpeed() * 10.0f * 0.33f) : 0.0f);
                limit += 0.02 * speed;
            }
            if (offsetH > limit) {
                ++this.illegalMovements;
            }
            else {
                ++this.legalMovements;
            }
            final int total = this.illegalMovements + this.legalMovements;

            if (total >= 20) {
                final double percentage = this.illegalMovements / 20.0 * 100.0;

                if (percentage >= 45.0 && alert(AlertType.RELEASE, player, String.format("P %.1f. VL %.1f/%s.", percentage, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyI") && vl >= ConfigurationManager.banVL("FlyI")) {
                        punish(player);
                    }
                }
                this.illegalMovements = 0;
                this.legalMovements = 0;
            }
        }
        playerData.setCheckVl(vl, this);
    }
}
