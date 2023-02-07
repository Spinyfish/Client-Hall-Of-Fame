package org.crystalpvp.anticheat.check.movement.fly;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FlyA extends PositionCheck {

    public FlyA(PlayerData playerData) {
        super(playerData, "Flight (A)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("FlyA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (!playerData.isInLiquid() && !playerData.isOnGround() && playerData.getVelocityV() == 0) {
            if (update.getFrom().getY() >= update.getTo().getY()) {
                return;
            }

            final double distance = playerData.getLastDistanceY();
            double limit = 2.0;

            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().equals(PotionEffectType.JUMP)) {
                        final int level = effect.getAmplifier() + 1;
                        limit += Math.pow(level + 4.2, 2.0) / 16.0;
                        break;
                    }
                }
            }

            if (distance > limit) {
                if (++vl >= 10 && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyA") && vl >= ConfigurationManager.banVL("FlyA")) {
                        punish(player);
                    }
                }
            } else {
                vl = 0;
            }
        } else {
            vl = 0;
        }

        playerData.setCheckVl(vl, this);
    }

}
