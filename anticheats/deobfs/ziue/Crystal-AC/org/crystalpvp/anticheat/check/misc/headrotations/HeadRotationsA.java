package org.crystalpvp.anticheat.check.misc.headrotations;

import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.RotationCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.RotationUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */


public class HeadRotationsA extends RotationCheck {
    public HeadRotationsA(PlayerData playerData) {
        super(playerData, "Headrotations (A)");
    }

    @Override
    public void handleCheck(Player player, RotationUpdate type) {
        if (!ConfigurationManager.isEnabled("HeadRotationsA")) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        if (player.getLocation().getPitch() > 90.0 || player.getLocation().getPitch() < -90.0) {
                if (this.alert(AlertType.RELEASE, player,
                        String.format("VL %.2f.",
                                ++vl), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("HeadRotationsA") && vl >= ConfigurationManager.banVL("HeadRotationsA")) {
                        punish(player);
                    }
                }
        }
        playerData.setCheckVl(vl, this);
    }
}
