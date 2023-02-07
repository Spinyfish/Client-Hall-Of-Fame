package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraB extends PacketCheck {
	private boolean sent;
	private boolean failed;
	private int movements;

	public KillAuraB(PlayerData playerData) {
		super(playerData, "Kill-Aura (B)");
	}

	@Override
	public void handleCheck(Player player, Packet packet) {
		if (ConfigurationManager.isEnabled("KillAuraB") == false) {
			return;
		}
		double vl = playerData.getCheckVl(this);

		if (playerData.isDigging() && !playerData.isInstantBreakDigging() &&
		    System.currentTimeMillis() - playerData.getLastDelayedMovePacket() > 220L &&
		    playerData.getLastMovePacket() != null &&
		    System.currentTimeMillis() - playerData.getLastMovePacket().getTimestamp() < 110L) {
			if (packet instanceof PacketPlayInBlockDig &&
			    ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
				movements = 0;
				vl = 0;
			} else if (packet instanceof PacketPlayInArmAnimation && movements >= 2) {
				if (sent) {
					if (!failed) {
						if (++vl >= 5) {
							alert(AlertType.EXPERIMENTAL, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
							if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraB") && vl >= ConfigurationManager.banVL("KillAuraB")) {
								punish(player);
							}
						}
						failed = true;
					}
				} else {
					sent = true;
				}
			} else if (packet instanceof PacketPlayInFlying) {
				final boolean b = false;
				failed = b;
				sent = b;
				++movements;
			}
		}
		playerData.setCheckVl(vl, this);
	}
}
