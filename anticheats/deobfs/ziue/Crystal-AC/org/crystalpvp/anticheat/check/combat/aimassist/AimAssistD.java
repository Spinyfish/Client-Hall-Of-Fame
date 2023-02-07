package org.crystalpvp.anticheat.check.combat.aimassist;

import org.crystalpvp.anticheat.api.checks.RotationCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.RotationUpdate;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AimAssistD extends RotationCheck {

	private int moves;
	private double lastPitchDeviation, lastMoves;
	public Queue<Float> moveIntervals = new LinkedList<>();
	private boolean movedPitch, isGcd;
	private double multiplier = Math.pow(2, 24);
	private float previous;

	public AimAssistD(PlayerData playerData) {
		super(playerData, "Aim (D)");
	}

	@Override
	public void handleCheck(final Player player, final RotationUpdate update) {
		if (!ConfigurationManager.isEnabled("AimAssistD")) {
			return;
		}

		double vl = playerData.getCheckVl(this);

		if (TimesUtil.differenceTimeSecond(playerData.getLastTeleport(), System.currentTimeMillis()) > 3
				|| TimesUtil.differenceTimeSecond(playerData.getRespawnTime(), System.currentTimeMillis()) > 3
				|| TimesUtil.differenceTimeSecond(playerData.getLastAttackPacket(), System.currentTimeMillis()) < 1) {
			vl = 0;
			return;
		}


		CrystalLocation pos = playerData.getLastMovePacket();
		CrystalLocation previousPos = playerData.getQueue1LastMovePacket();

		if (pos == null || previousPos == null) {
			vl = 0;
		}

		float pitchChange = MathUtil.getDistanceBetweenAngles(update.getTo().getPitch(), update.getFrom().getPitch());

		if (pitchChange != 0) {
			movedPitch = true;
		} else {
			movedPitch = false;
		}

		if (!playerData.isDigging() && !playerData.isPlacing() && player.getGameMode().equals(GameMode.SURVIVAL)
				&& (System.currentTimeMillis() - playerData.getLastDelayedMovePacket() < 101L)) {
			if (movedPitch) {
				if (moveIntervals.add(pitchChange) && moveIntervals.size() == 20) {
					double average = moveIntervals.stream().mapToDouble(d -> d).average().orElse(0.0);

					double pitchDeviation = 0.0;

					for (Float d : moveIntervals)
						pitchDeviation += Math.pow(d.doubleValue() - average, 2.0);

					pitchDeviation /= moveIntervals.size();

					long a = (long) (pitchChange * multiplier);
					long b = (long) (previous * multiplier);

					long gcd = gcd(a, b);

					if (gcd < 0x20000) {
						isGcd = true;
					} else {
						isGcd = false;
					}
					float magicVal = pitchChange * 100 / previous;

					if (lastMoves == moves && lastPitchDeviation - pitchDeviation < 0.04
							&& lastPitchDeviation - pitchDeviation > -0.04 && isGcd && magicVal < 50) {
						if (++vl > 1) {


							alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

							if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AimAssistD") && vl >= ConfigurationManager.banVL("AimAssistD")) {
								punish(player);
							}



						}
					} else {
						vl = Math.max(0, vl - 0.125);
					}

					lastMoves = moves;
					lastPitchDeviation = pitchDeviation;
					moveIntervals.clear();
					moves = 0;
				}
			}

			movedPitch = false;
			++moves;
		}
		previous = pitchChange;
		playerData.setCheckVl(vl, this);
	}


	private long gcd(long a, long b) {
		if (b <= 0x4000) {
			return a;
		}

		return gcd(b, a % b);
	}

}
