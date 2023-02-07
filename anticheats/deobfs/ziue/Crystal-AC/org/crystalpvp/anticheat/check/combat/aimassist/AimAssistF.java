package org.crystalpvp.anticheat.check.combat.aimassist;

import org.crystalpvp.anticheat.api.checks.RotationCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.RotationUpdate;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AimAssistF extends RotationCheck {
    private float suspiciousYaw;

    public AimAssistF(PlayerData playerData) {
        super(playerData, "Aim (F)");
    }

    @Override
    public void handleCheck(final Player player, final RotationUpdate update) {
        if (!ConfigurationManager.isEnabled("AimAssistF")) {
            return;
        }

        if (TimesUtil.differenceTimeSecond(playerData.getLastAttackPacket(), System.currentTimeMillis()) > 1) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        final double diffYaw = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());
        final double diffPitch = MathUtil.getDistanceBetweenAngles(update.getTo().getPitch(), update.getFrom().getPitch());

        if (diffPitch > 2 && diffYaw < 0.09 && ++vl >= 20) {
            alert(AlertType.RELEASE, player, String.format("Yaw 0,0. Pitch %.1f. VL %.1f/%s.", diffYaw, diffPitch, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AimAssistF") && vl >= ConfigurationManager.banVL("AimAssistF")) {
                punish(player);
            }
        }
        else {
            vl -= 0.05;
        }

        playerData.setCheckVl(vl, this);
    }
}
