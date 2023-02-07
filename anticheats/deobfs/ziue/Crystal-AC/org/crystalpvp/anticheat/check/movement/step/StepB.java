package org.crystalpvp.anticheat.check.movement.step;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class StepB extends PositionCheck {

    public StepB(PlayerData playerData) {
        super(playerData, "JumpStep (B)");
    }

    private double lastHeight;
    private boolean cancel;
    private ArrayList<Block> blocks = new ArrayList<>();

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("StepB") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.isOnPiston() || playerData.isWasOnPiston()) {
            return;
        }

        double height = MathUtil.doubleDecimal(playerData.getLastDistanceY(), 2);


        if (height + lastHeight == 1.00 && lastHeight != 0 && height != 0) {


            blocks = BlockUtil.getBlocksAround(player.getLocation(), 2);

            for (int i = 0; i < blocks.size(); ++i) {
                if (String.format("%s", blocks.get(i)).contains("STAIR")
                        || String.format("%s", blocks.get(i)).contains("SLAB")) {
                    cancel = true;
                }
            }

            if (!cancel) {

                alert(AlertType.RELEASE, player, String.format("Height %.2f. LastHeight %.2f. VL %.1f/%s.", height, lastHeight, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);


                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("StepB") && vl >= ConfigurationManager.banVL("StepB")) {
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
