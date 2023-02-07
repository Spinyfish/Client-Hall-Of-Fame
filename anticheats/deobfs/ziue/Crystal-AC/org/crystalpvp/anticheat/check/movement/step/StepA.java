package org.crystalpvp.anticheat.check.movement.step;


import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class StepA extends PositionCheck {

	public StepA(PlayerData playerData) {
		super(playerData, "Step (A)");
	}

	private boolean cancel;
	private double lastHeight;
	private ArrayList<Block> blocks = new ArrayList<>();

	@Override
	public void handleCheck(final Player player, final PositionUpdate update) {
		if (ConfigurationManager.isEnabled("StepA") == false) {
			return;
		}
		double vl = playerData.getCheckVl(this);

		if (playerData.isOnPiston() || playerData.isWasOnPiston()) {
			return;
		} else if (TimesUtil.differenceTimeSecond(playerData.getLastTeleport(), System.currentTimeMillis()) < 5) {
			return;
		} else if (TimesUtil.differenceTimeSecond(playerData.getLastDamagePacket(), System.currentTimeMillis()) < 3) {
			return;
		}

		double height = playerData.getLastDistanceY();


		if ((height * -1 > 0.99 || height > 0.99) && lastHeight == 0 && playerData.isOnGround() && playerData.isWasOnGround() && !player.hasPotionEffect(PotionEffectType.JUMP) && !player.isInsideVehicle()) {


			blocks = BlockUtil.getBlocksAround(player.getLocation(), 4);
			for (int i = 0; i < blocks.size(); ++i) {
				if (String.format("%s", blocks.get(i)).contains("SLIME")) {
					cancel = true;
				}
			}

			if (!cancel) {
				alert(AlertType.RELEASE, player, String.format("BlockUp %.2f. VL %.1f/%s.", height, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);


				if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("StepA") && vl >= ConfigurationManager.banVL("StepA")) {
					punish(player);
				}
			}


		} else {
			vl -= 0.005;
		}

		cancel = false;
		lastHeight = height;
		playerData.setCheckVl(vl, this);
	}

}
