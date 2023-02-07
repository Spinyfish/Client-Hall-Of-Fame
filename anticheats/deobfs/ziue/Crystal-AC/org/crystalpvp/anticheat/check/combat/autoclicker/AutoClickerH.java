package org.crystalpvp.anticheat.check.combat.autoclicker;

import net.minecraft.server.v1_8_R3.*;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerH extends PacketCheck {

	private int ticks = 0;
	private Queue<Integer> tickList = new ConcurrentLinkedQueue<Integer>();

	public AutoClickerH(PlayerData playerData) {
		super(playerData, "Auto-Clicker (H)");
	}

	@Override
	public void handleCheck(Player player, Packet packet) {
		if (ConfigurationManager.isEnabled("AutoClickerH") == false) {
			return;
		}
		double vl = playerData.getCheckVl(this);

		if (packet instanceof PacketPlayInFlying) {
			if (playerData.isDigging() && !playerData.isAbortedDigging()) {
				++this.ticks;
			}
		} else if (packet instanceof PacketPlayInBlockDig) {
			PacketPlayInBlockDig packetPlayInBlockDig = (PacketPlayInBlockDig)packet;
			if (packetPlayInBlockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
				this.ticks = 0;
			} else if (packetPlayInBlockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
				BlockPosition blockPosition = packetPlayInBlockDig.a();
				EnumDirection enumDirection = packetPlayInBlockDig.b();
				if (blockPosition.equals((Object)playerData.getDiggingBlock()) && !enumDirection.equals((Object)playerData.getDiggingBlockFace())) {
					this.tickList.add(this.ticks);
					if (this.tickList.size() > 40) {
						double deviation = MathUtil.deviation(this.tickList);
						if (deviation < 0.325) {
							alert(AlertType.RELEASE, player, String.format("Ticks %s. VL %.1f/%s.", ticks, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

							if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerH") && vl >= ConfigurationManager.banVL("AutoClickerH")) {
								punish(player);
							}
						} else {
							vl -= Math.min(vl + 2.0, 0.25);
						}
						this.tickList.clear();
					}
				}
				this.ticks = 0;
			}
		}

		playerData.setCheckVl(vl, this);
	}
}
