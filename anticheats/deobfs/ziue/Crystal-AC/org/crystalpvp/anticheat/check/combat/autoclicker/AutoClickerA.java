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

public class AutoClickerA extends PacketCheck {

	private int swings;
	private int movements;

	public AutoClickerA(PlayerData playerData) {
		super(playerData, "Auto-Clicker (A)");
	}

	@Override
	public void handleCheck(Player player, Packet packet) {
		if (ConfigurationManager.isEnabled("AutoClickerA") == false) {
			return;
		}
		double vl = playerData.getCheckVl(this);

		if (packet instanceof PacketPlayInArmAnimation
				&& !playerData.isDigging() && !playerData.isPlacing() && !playerData.isFakeDigging()
				&& (System.currentTimeMillis() - playerData.getLastDelayedMovePacket()) > 220L
				&& (System.currentTimeMillis() - playerData.getLastMovePacket().getTimestamp()) < 110L) {
			++swings;
		} else if (packet instanceof PacketPlayInFlying && ++movements == 20) {
			if (swings > ConfigurationManager.getInfoInt("AutoClickerA", "maxCps")) {
				alert(AlertType.RELEASE, player, String.format("Clicks %s. VL %.1f/%s.", swings, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

				if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerA") && vl >= ConfigurationManager.banVL("AutoClickerA")) {
					punish(player);
				}
			}

			playerData.setLastCps(swings);
			movements = swings = 0;
			playerData.setCheckVl(vl, this);
		}
	}

}
