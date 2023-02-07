package org.crystalpvp.anticheat.check.movement.step;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class StepC extends PositionCheck {

    public StepC(PlayerData playerData) {
        super(playerData, "JumpStep (C)");
    }

    private double lastHeight;
    private double totalHeight;

    private int stage;

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("StepC") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.isOnPiston() || playerData.isWasOnPiston()) {
            return;
        }


        double height = MathUtil.doubleDecimal(playerData.getLastDistanceY(), 2);

        //Bukkit.broadcastMessage(String.format("Height %s. LHeight %s. First %s. Second %s.", height, lastHeight , MathUtil.doubleDecimal(lastHeight - 0.10, 2), MathUtil.doubleDecimal(lastHeight - 0.09, 2)));

        if (stage < 7 && (height == MathUtil.doubleDecimal(lastHeight - 0.10, 2) || height == MathUtil.doubleDecimal(lastHeight - 0.09, 2))) {

            ++stage;

            if (totalHeight == 0) {
                totalHeight += lastHeight;
            }

            totalHeight = MathUtil.doubleDecimal(totalHeight += height, 2);


            if (totalHeight == 1.00 || totalHeight == 1.01) {

                alert(AlertType.RELEASE, player, String.format("Stage %s. TotalHeight %.2f. VL %.1f/%s.", stage, totalHeight, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);


                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("StepC") && vl >= ConfigurationManager.banVL("StepC")) {
                    punish(player);
                }
            }

        } else {
            totalHeight = stage = 0;
        }


        lastHeight = height;
        playerData.setCheckVl(vl, this);
    }

}
