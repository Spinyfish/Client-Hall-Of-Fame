package org.crystalpvp.anticheat.check.movement.fly;

import org.bukkit.Location;
import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */


public class FlyC extends PositionCheck {

    private int illegalMovements;
    private int legalMovements;

    public FlyC(PlayerData playerData) {
        super(playerData, "Flight/Speed (C)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("FlyC")) {
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

            double threshold;

            if (playerData.isOnGround()) {
                threshold = 0.34;

                if (playerData.isOnStairs()) {
                    threshold = 0.45;
                } else if (playerData.isOnIce() || playerData.getMovementsSinceIce() < 40) {
                    if (playerData.isUnderBlock()) {
                        threshold = 1.3;
                    } else {
                        threshold = 0.8;
                    }
                } else if (playerData.isUnderBlock() || playerData.getMovementsSinceUnderBlock() < 40) {
                    threshold = 0.7;
                } else if (playerData.isOnCarpet()) {
                    threshold = 0.7;
                }

                threshold += 0.06 * speed;
            } else {
                threshold = 0.36;

                if (playerData.isOnStairs()) {
                    threshold = 0.45;
                } else if (playerData.isOnIce() || playerData.getMovementsSinceIce() < 40) {
                    if (playerData.isUnderBlock()) {
                        threshold = 1.3;
                    } else {
                        threshold = 0.8;
                    }
                } else if (playerData.isUnderBlock() || playerData.getMovementsSinceUnderBlock() < 40) {
                    threshold = 0.7;
                } else if (playerData.isOnCarpet()) {
                    threshold = 0.7;
                }

                threshold += 0.02 * speed;
            }

            threshold += ((player.getWalkSpeed() > 0.2f) ? (player.getWalkSpeed() * 10.0f * 0.33f) : 0.0f);

            if (offsetH > threshold) {
                ++illegalMovements;
            } else {
                ++legalMovements;
            }

            final int total = illegalMovements + legalMovements;

            if (total == 20) {
                final double percentage = illegalMovements / 20.0 * 100.0;

                if (percentage >= 45.0 && alert(AlertType.RELEASE, player, String.format("P %.1f. VL %.1f/%s.", percentage, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {
                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyC") && vl >= ConfigurationManager.banVL("FlyC")) {
                        punish(player);
                    }
                }

                final boolean b = false;
                legalMovements = (b ? 1 : 0);
                illegalMovements = (b ? 1 : 0);
            }
        }
        playerData.setCheckVl(vl, this);
    }

}
