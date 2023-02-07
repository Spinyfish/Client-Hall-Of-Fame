package org.crystalpvp.anticheat.check.combat.aimassist;

import org.crystalpvp.anticheat.api.checks.RotationCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.RotationUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AimAssistA extends RotationCheck {

    private float suspiciousYaw;
    
    public AimAssistA(PlayerData playerData) { super(playerData, "Aim (A)"); }
    
    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if (ConfigurationManager.isEnabled("AimAssistA") == false) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        if (System.currentTimeMillis() - playerData.getLastAttackPacket() > 10000L) {
            return;
        }

        final float diffYaw = playerData.getLastDiffYaw();

        if (diffYaw > 1.0f && Math.round(diffYaw) == diffYaw && diffYaw % 1.5f != 0.0f) {
            if (diffYaw == suspiciousYaw) {
                alert(AlertType.RELEASE, player, String.format("Yaw %.1f. VL %.1f/%s", diffYaw, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AimAssistA") && vl >= ConfigurationManager.banVL("AimAssistA")) {
                    punish(player);
                }
            }

            suspiciousYaw = Math.round(diffYaw);
        } else {
            suspiciousYaw = 0.0f;
        }
        playerData.setCheckVl(vl, this);
    }

}
