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

public class AimAssistG extends RotationCheck {

    public AimAssistG(PlayerData playerData) {
        super(playerData, "Aim (G)");
    }

    @Override
    public void handleCheck(final Player player, final RotationUpdate update) {
        if (!ConfigurationManager.isEnabled("AimAssistG")) {
            return;
        }

        if (System.currentTimeMillis() - playerData.getLastAttackPacket() >= 10000L) {
            return;
        }

        double vl = playerData.getCheckVl(this);
        double lastDiffYaw = playerData.getLastYawH();
        double lastDiffPitch = playerData.getLastPitchH();

        final double diffYaw = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());
        final double diffPitch = MathUtil.getDistanceBetweenAngles(update.getTo().getPitch(), update.getFrom().getPitch());

        if (diffYaw > lastDiffYaw && diffPitch == lastDiffPitch && lastDiffPitch > 3 && lastDiffPitch > 3 && ++vl > 10) {

            alert(AlertType.RELEASE, player, String.format("Yaw %.1f. Pitch %.1f. VL %.1f/%s.", diffYaw, diffPitch, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AimAssistG") && vl >= ConfigurationManager.banVL("AimAssistG")) {
                punish(player);
            }
        }
        else {
            vl -= 0.05;
        }

        playerData.setLastPitchH(diffPitch);
        playerData.setLastYawH(diffYaw);
        playerData.setCheckVl(vl, this);
    }
}
