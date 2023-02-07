package org.crystalpvp.anticheat.check.movement.step;

import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class StepD extends PositionCheck {

    public StepD(PlayerData playerData) {
        super(playerData, "JumpStep (D)");
    }

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("StepD") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        final double height = 0.9;
        final double differece = update.getTo().getY() - update.getFrom().getY();
        if(differece > height) {
            alert(AlertType.EXPERIMENTAL, player, String.format("Height %.2f. VL %.1f/%s.", height, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("StepD") && vl >= ConfigurationManager.banVL("StepD")) {
                punish(player);
            }
        }
        playerData.setCheckVl(vl, this);
    }
}
