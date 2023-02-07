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

public class AimAssistC extends RotationCheck {

    private double streak, oldPitch;

    public AimAssistC(PlayerData playerData) {
        super(playerData, "Aim (C)");
    }

    @Override
    public void handleCheck(final Player player, final RotationUpdate update) {
        if (!ConfigurationManager.isEnabled("AimAssistC")) {
            return;
        }

        double vl = playerData.getCheckVl(this);


        if (TimesUtil.differenceTimeSecond(playerData.getLastTeleport(), System.currentTimeMillis()) > 3
                || TimesUtil.differenceTimeSecond(playerData.getRespawnTime(), System.currentTimeMillis()) > 3
                || TimesUtil.differenceTimeSecond(playerData.getLastAttackPacket(), System.currentTimeMillis()) < 2) {
            vl = streak = 0;
            return;
        }


        float pitchChange = MathUtil.getDistanceBetweenAngles(update.getTo().getPitch(), update.getFrom().getPitch());
        float yawChange = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());

        double pitchDifference = oldPitch - pitchChange;

        if (yawChange >= 2) {
            if (pitchChange != 0 && pitchDifference == 0) {
                if (++vl > 1) {
                    ++streak;
                } else {
                    streak = Math.max(0, streak - 0.5);
                }
                if (streak > 10) {

                    alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AimAssistC") && vl >= ConfigurationManager.banVL("AimAssistC")) {
                        punish(player);
                    }

                    streak = 0;
                }
            } else {
                vl = Math.max(0, vl - 0.5);
                streak = Math.max(0, streak - 0.5);
            }
        }

        oldPitch = pitchChange;
        playerData.setCheckVl(vl, this);
    }

}
