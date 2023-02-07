package org.crystalpvp.anticheat.check.movement.fly;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FlyG extends PositionCheck {

    public FlyG(PlayerData playerData) {
        super(playerData, "Flight/Jetpack (G)");
    }

    private boolean cancel;
    private double height;
    private double lastHeight;
    private double lastYDiff;
    private int duplicateY;
    private ArrayList<Block> blocks = new ArrayList<>();

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("FlyG")) {
            return;
        }


        if (TimesUtil.differenceTimeSecond(playerData.getLastDamagePacket(), System.currentTimeMillis()) < 2) {
            return;
        }

        double vl = playerData.getCheckVl(this);
        double height = MathUtil.doubleDecimal(playerData.getLastDistanceY(), 2);

        if (lastYDiff == height - lastHeight && height != 0) {
            duplicateY++;
        } else {
            duplicateY = 0;
        }





        if (duplicateY > 7) {

            blocks = BlockUtil.getBlocksAround(player.getLocation(), 2);
            for (int i = 0; i < blocks.size(); ++i) {
                if (String.format("%s", blocks.get(i)).contains("LAVA")
                        || String.format("%s", blocks.get(i)).contains("WATER")
                        || String.format("%s", blocks.get(i)).contains("SLIME")
                        || String.format("%s", blocks.get(i)).contains("WEB")
                        || String.format("%s", blocks.get(i)).contains("LADDER")
                        || String.format("%s", blocks.get(i)).contains("VINE")
                        || String.format("%s", blocks.get(i)).contains("STAIR")) {
                    cancel = true;
                    break;
                }
            }

            if (!cancel) {

                if (++vl > 5) {

                    alert(AlertType.RELEASE, player, String.format("Duplicate %s. DiffY %s. VL %.1f/%s.", duplicateY, lastYDiff, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyG") && vl >= ConfigurationManager.banVL("FlyG")) {
                        punish(player);
                    }

                }
            }

        }




        lastYDiff = height - lastHeight;
        lastHeight = height;
        cancel = false;
        playerData.setCheckVl(vl, this);
    }
}