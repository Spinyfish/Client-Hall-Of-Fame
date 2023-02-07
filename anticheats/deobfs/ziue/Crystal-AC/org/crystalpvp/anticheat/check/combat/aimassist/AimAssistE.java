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

public class AimAssistE extends RotationCheck {

	private float lastPitchRate;
	private float lastYawRate;

	public AimAssistE(PlayerData playerData) {
		super(playerData, "Aim (E)");
	}

	@Override
	public void handleCheck(Player player, RotationUpdate update) {
		if (ConfigurationManager.isEnabled("AimAssistE") == false) {
			return;
		}
		double vl = playerData.getCheckVl(this);

		if (System.currentTimeMillis() - playerData.getLastAttackPacket() > 10000L) {
			return;
		}

		float diffPitch = MathUtil.getDistanceBetweenAngles(update.getTo().getPitch(), update.getFrom().getPitch());
		float diffYaw = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());

		float diffYawPitch = Math.abs(diffYaw - diffPitch);

		float diffPitchRate = Math.abs(lastPitchRate - diffPitch);
		float diffYawRate = Math.abs(lastYawRate - diffYaw);

		float diffPitchRatePitch = Math.abs(diffPitchRate - diffPitch);
		float diffYawRateYaw = Math.abs(diffYawRate - diffYaw);

		if (diffYaw > 0.05f && diffPitch > 0.05 && (diffPitchRate > 1.0 || diffYawRate > 1.0) &&
		    (diffPitchRatePitch > 1.0f || diffYawRateYaw > 1.0f) && diffYawPitch < 0.009f && diffYawPitch > 0.001f) {
			alert(AlertType.RELEASE, player, String.format("DYP %.3f. DP %.3f. DY %.3f. DPR %.3f. DYR %.3f. DPRP %.3f. DYRY %.3f. VL %.1f/%s.", diffYawPitch, diffYaw, diffPitch, diffPitchRate, diffYawRate, diffPitchRatePitch, diffYawRateYaw, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

			if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AimAssistE") && vl >= ConfigurationManager.banVL("AimAssistE")) {
				punish(player);
			}
		}

		lastYawRate = diffYaw;
		lastPitchRate = diffPitch;
		playerData.setCheckVl(vl, this);
	}

}
