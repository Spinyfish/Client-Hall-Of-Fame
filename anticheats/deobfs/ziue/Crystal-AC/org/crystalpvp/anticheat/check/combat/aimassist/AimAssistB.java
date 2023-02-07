package org.crystalpvp.anticheat.check.combat.aimassist;

import org.crystalpvp.anticheat.api.checks.RotationCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.RotationUpdate;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AimAssistB extends RotationCheck {

    private float suspiciousYaw;
    
    public AimAssistB(PlayerData playerData) {
        super(playerData, "Aim (B)");
    }
    
    @Override
    public void handleCheck(Player player, RotationUpdate update) {
        if (!ConfigurationManager.isEnabled("AimAssistB")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (System.currentTimeMillis() - playerData.getLastAttackPacket() > 10000L) {
            return;
        }

        final float diffYaw = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());

        if (diffYaw > 1.0f && Math.round(diffYaw * 10.0f) * 0.1f == diffYaw && Math.round(diffYaw) != diffYaw && diffYaw % 1.5f != 0.0f) {
            if (diffYaw == suspiciousYaw) {
                alert(AlertType.RELEASE, player,  String.format("Yaw %.1f. VL %.1f/%s", diffYaw, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AimAssistB") && vl >= ConfigurationManager.banVL("AimAssistB")) {
                    punish(player);
                }
            }

            suspiciousYaw = Math.round(diffYaw * 10.0f) * 0.1f;
        } else {
            suspiciousYaw = 0.0f;
        }
        playerData.setCheckVl(vl, this);
    }

}
