package org.crystalpvp.anticheat.check.combat.autoclicker;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerJ extends PacketCheck {

	private int swings;
	private int movements;

	public AutoClickerJ(PlayerData playerData) {
		super(playerData, "Auto-Clicker (J)");
	}

	@Override
	public void handleCheck(Player player, Packet packet) {
		if (ConfigurationManager.isEnabled("AutoClickerJ") == false) {
			return;
		}
		double vl = playerData.getCheckVl(this);

		int lastCps = playerData.getLastCpsJ();
		int autoM = playerData.getAutoClickerJ();

		if (packet instanceof PacketPlayInArmAnimation
				&& !playerData.isDigging() && !playerData.isPlacing() && !playerData.isFakeDigging()
				&& (System.currentTimeMillis() - playerData.getLastDelayedMovePacket()) > 220L
				&& (System.currentTimeMillis() - playerData.getLastMovePacket().getTimestamp()) < 110L) {
			++swings;
		} else if (packet instanceof PacketPlayInFlying && ++movements == 20) {

			if (swings == lastCps && swings > 8) {

				if (autoM > 3 && ++vl > 5) {

					alert(AlertType.RELEASE, player, String.format("Clicks %s. Duplicate %s. VL %.1f/%s.", swings, autoM - 6, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

					if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerJ") && vl >= ConfigurationManager.banVL("AutoClickerJ")) {
						punish(player);
					}


				}

				playerData.setAutoClickerJ(autoM + 1);
			}

			else if (swings > 8) {
				--vl;
				playerData.setAutoClickerJ(0);
			}

			playerData.setLastCpsJ(swings);
			movements = swings = 0;
		}

		playerData.setCheckVl(vl, this);
	}

}
