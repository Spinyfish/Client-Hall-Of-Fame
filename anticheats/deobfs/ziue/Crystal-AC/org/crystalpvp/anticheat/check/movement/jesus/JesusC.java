package org.crystalpvp.anticheat.check.movement.jesus;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class JesusC extends PositionCheck {

    private Location loc;
    private boolean noY;

    public JesusC(PlayerData playerData) {
        super(playerData, "Jesus (C)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (!ConfigurationManager.isEnabled("JesusC")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        loc = player.getLocation();

        if (BlockUtil.isOnPlatform(loc, "WATER") && !BlockUtil.blockAt(loc, "AIR")
                && !player.isInsideVehicle()
                && update.getFrom().getY() - update.getTo().getY() == 0.00000
                && player.getGameMode() != GameMode.CREATIVE && !player.isFlying()
                && !BlockUtil.isBlockingVelocityH(player.getLocation())) {

            if (noY) {
                if (++vl >= 3) {
                    alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("JesusC") && vl >= ConfigurationManager.banVL("JesusC")) {
                        punish(player);
                    }
                }
            } else {
                noY = true;
            }

        } else {
            noY = false;
        }


        playerData.setCheckVl(vl, this);
    }

}
