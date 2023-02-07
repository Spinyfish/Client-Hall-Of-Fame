package org.crystalpvp.anticheat.check.movement.fly;

import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.CrystalUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FlyD extends PositionCheck {

    public FlyD(PlayerData playerData) {
        super(playerData, "Flight (D)");
    }

    private boolean cancel;
    private List<String> permission;

    @Override
    public void handleCheck(final Player player, final PositionUpdate update) {
        if (ConfigurationManager.isEnabled("FlyD") == false) {
            return;
        }

        double vl = playerData.getCheckVl(this);


        if (player.isFlying() && !player.isOp() && (player.getGameMode() != GameMode.CREATIVE)) {

            permission.clear();
            permission = ConfigurationManager.getInfoStringList("FlyD", "flyPermission");

            for (int i = 0; i < permission.size(); i++) {
                if (CrystalUtil.hasPermission(player, permission.get(i))) {
                    cancel = true;
                }
            }

            if (!cancel) {


                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);


                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyD") && vl >= ConfigurationManager.banVL("FlyD")) {
                    punish(player);
                }

            }
        }

        cancel = false;
        playerData.setCheckVl(vl, this);
    }
}
