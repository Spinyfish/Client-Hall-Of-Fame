package org.crystalpvp.anticheat.check.movement.fly;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FlyH extends PositionCheck {

    public FlyH(PlayerData playerData) {
        super(playerData, "Flight (H)");
    }

    private double height;

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("FlyH")) {
            return;
        }

        double vl = playerData.getCheckVl(this);
        double height = playerData.getLastDistanceY();

        if (String.format("%s", height).contains("E-5")) {

            if (++vl > 5) {

                alert(AlertType.RELEASE, player, String.format("Height %s. VL %.1f/%s.", height, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyH") && vl >= ConfigurationManager.banVL("FlyH")) {
                    punish(player);
                }

            }

        }


        playerData.setCheckVl(vl, this);
    }
}