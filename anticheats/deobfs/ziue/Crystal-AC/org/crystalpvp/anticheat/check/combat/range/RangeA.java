package org.crystalpvp.anticheat.check.combat.range;


import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class RangeA extends PacketCheck {

	private boolean sameTick;

	public RangeA(PlayerData playerData) {
		super(playerData, "Range (A)");
	}

	@Override
	public void handleCheck(final Player player, final Packet packet) {
		if (!ConfigurationManager.isEnabled("RangeA")) {
			return;
		}
		double vl = playerData.getCheckVl(this);

		if (packet instanceof PacketPlayInUseEntity && !player.getGameMode().equals((Object) GameMode.CREATIVE) && System.currentTimeMillis() - playerData.getLastDelayedMovePacket() > 110L && System.currentTimeMillis() - playerData.getLastMovePacket().getTimestamp() < 110L && !sameTick) {
			final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity)packet;
			if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
				final Entity targetEntity = useEntity.a(((CraftPlayer)player).getHandle().getWorld());



				if (targetEntity instanceof EntityPlayer) {
					final Player target = (Player) targetEntity.getBukkitEntity();
					final CrystalLocation targetCrystalLocation = playerData.getLastPlayerPacket(target.getUniqueId(), MathUtil.pingFormula(playerData.getPing()));

					CrystalLocation playerCrystalLocation = playerData.getLastMovePacket();
					PlayerData targetData = getPlugin().getPlayerDataManager().getPlayerData(target);

					if (targetData == null) {
						return;
					}
					if (targetCrystalLocation == null) {
						return;
					}

					long diff = System.currentTimeMillis() - targetCrystalLocation.getTimestamp();
					long estimate = MathUtil.pingFormula(playerData.getPing()) * 50L;
					long diffEstimate = diff - estimate;

					final double ping = playerData.getPing();
					final double pingTarget = targetData.getPing();

					if (diffEstimate >= 500L) {
						return;
					}

					double threshold = pingTarget * 0.005 + ping * 0.005;


					double range = targetCrystalLocation.getY() - playerCrystalLocation.getY();

					if (range > 0) {
						range -= 1.999;
					} else {
						range += 0.999;
					}
					if (range >= 3.0 || range <= -3.0) {
						range += 0.01;
						if (++vl >= 20) {
							if (alert(AlertType.RELEASE, player, String.format("Range %.2f. Ping %.1f PingTarget %.1f VL %.1f/%s.", range, ping, pingTarget, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {
								if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("RangeA") && vl >= ConfigurationManager.banVL("RangeA")) {
									punish(player);
								}
							} else {
								vl = 0.0;
							}
						}
					}
					else if (range <= 2.0) {
						vl -= 0.25;
					}
					else if (range >= 2.0) {
						vl -= 0.225;
					}
					sameTick = true;
				}
			}
		}
		else if (packet instanceof PacketPlayInFlying) {
			sameTick = false;
		}
		playerData.setCheckVl(vl, this);
	}
}
