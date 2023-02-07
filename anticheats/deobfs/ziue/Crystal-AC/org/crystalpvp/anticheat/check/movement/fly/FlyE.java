package org.crystalpvp.anticheat.check.movement.fly;

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

public class FlyE extends PositionCheck {

    public FlyE(PlayerData playerData) {
        super(playerData, "Flight (E)");
    }

    private boolean cancel;
    private double height;
    private double lastHeight;
    private int stage;
    private ArrayList<Block> blocks = new ArrayList<>();

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("FlyE")) {
            return;
        }

        double vl = playerData.getCheckVl(this);
        double height = MathUtil.doubleDecimal(playerData.getLastDistanceY(), 2);


        if (height < 0 && stage == 0 && height != 0) {
            ++stage;
        } else if (stage == 1 && height == 0.09999999999999432) {

            blocks = BlockUtil.getBlocksAround(player.getLocation(), 2);
            for (int i = 0; i < blocks.size(); ++i) {
                if (String.format("%s", blocks.get(i)).contains("LAVA")
                        || String.format("%s", blocks.get(i)).contains("WATER")
                        || String.format("%s", blocks.get(i)).contains("SLIME")) {
                    cancel = true;
                    break;
                }
            }

            if (!cancel) {

                if (++vl > 5) {

                    alert(AlertType.RELEASE, player, String.format("LastHeight %s. Height %s. VL %.1f/%s.", lastHeight, height, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyE") && vl >= ConfigurationManager.banVL("FlyE")) {
                        punish(player);
                    }

                }
            }
        }

        lastHeight = height;
        cancel = false;
        playerData.setCheckVl(vl, this);
    }
}