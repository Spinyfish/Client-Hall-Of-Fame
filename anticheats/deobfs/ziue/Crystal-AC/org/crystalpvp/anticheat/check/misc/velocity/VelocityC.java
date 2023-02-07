package org.crystalpvp.anticheat.check.misc.velocity;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
		import org.crystalpvp.anticheat.api.event.player.AlertType;
		import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
		import org.crystalpvp.anticheat.api.update.PositionUpdate;
		import org.crystalpvp.anticheat.data.player.PlayerData;
		import net.minecraft.server.v1_8_R3.EntityPlayer;
		import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
		import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class VelocityC extends PositionCheck {

	public VelocityC(PlayerData playerData) {
		super(playerData, "Velocity (C)");
	}

	@Override
	public void handleCheck(final Player player, final PositionUpdate update) {
		if (!ConfigurationManager.isEnabled("VelocityC")) {
			return;
		}
		double vl = playerData.getCheckVl(this);

		double offsetY = update.getTo().getY() - update.getFrom().getY();
		double offsetH = Math.hypot(update.getTo().getX() - update.getFrom().getX(),
				update.getTo().getZ() - update.getFrom().getZ());

		double velocityH = Math.hypot(playerData.getVelocityX(), playerData.getVelocityZ());

		EntityPlayer entityPlayer = ((CraftPlayer) update.getPlayer()).getHandle();
		if (playerData.getVelocityY() > 0.0 && playerData.isWasOnGround() &&
				!playerData.isUnderBlock() && !playerData.isWasUnderBlock() && !playerData.isInLiquid() &&
				!playerData.isWasInLiquid() && !playerData.isInWeb() && !playerData.isWasInWeb() &&
				update.getFrom().getY() % 1.0 == 0.0 && offsetY > 0.0 && offsetY < 0.41999998688697815 &&
				velocityH > 0.45 && !entityPlayer.world.c(entityPlayer.getBoundingBox().grow(1.0, 0.0, 1.0))) {
			double ratio = offsetH / velocityH;
			if (ratio < 0.62) {
				if ((vl += 1.1) >= 8.0) {
					alert(AlertType.RELEASE, player, String.format("P %s. VL %.1f/%s.", Math.round(ratio * 100.0), vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
				}
				if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("VelocityC") && vl >= ConfigurationManager.banVL("VelocityC")) {
					punish(player);
				}
			} else {
				vl -= 0.4;
			}
			playerData.setCheckVl(vl, this);
		}
	}

}