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

public class FlyB extends PositionCheck {

    public FlyB(PlayerData playerData) {
        super(playerData, "Flight (B)");
    }
    
    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("FlyB") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (!playerData.isInLiquid() && !playerData.isOnGround()) {
            final double offsetH = Math.hypot(update.getTo().getX() - update.getFrom().getX(), update.getTo().getZ() - update.getFrom().getZ());
            final double offsetY = update.getTo().getY() - update.getFrom().getY();

            if (offsetH > 0.0 && offsetY == 0.0) {
                if (++vl >= 10 && alert(AlertType.RELEASE, player, String.format("H %.2f. VL %.1f/%s.", offsetH, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyB") && vl >= ConfigurationManager.banVL("FlyB")) {
                        punish(player);
                    }
                }
            } else {
                vl = 0;
            }
        } else {
            vl = 0;
        }

        playerData.setCheckVl(vl, this);
    }

}
