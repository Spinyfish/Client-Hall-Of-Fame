package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.AbstractCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraN extends AbstractCheck<int[]> {

    private int doubleSwings;
    private int doubleAttacks;
    private int bareSwings;

    public KillAuraN(PlayerData playerData) {
        super(playerData, int[].class, "Kill-Aura (N)");
    }
    
    @Override
    public void handleCheck(final Player player, final int[] ints) {
        if (ConfigurationManager.isEnabled("KillAuraN") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        final int swings = ints[0];
        final int attacks = ints[1];

        if (swings > 1 && attacks == 0) {
            ++doubleSwings;
        } else if (swings == 1 && attacks == 0) {
            ++bareSwings;
        } else if (attacks > 1) {
            ++doubleAttacks;
        }

        if (doubleSwings + doubleAttacks == 20) {

            if (doubleSwings == 0) {
                if (bareSwings > 10 && ++vl > 3.0) {
                    alert(AlertType.EXPERIMENTAL, player, String.format("BS %.1f. VL %.1f/%s.", bareSwings, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraN") && vl >= ConfigurationManager.banVL("KillAuraN")) {
                        punish(player);
                    }
                }
            } else {
                --vl;
            }

            doubleSwings = 0;
            doubleAttacks = 0;
            bareSwings = 0;
        }
        playerData.setCheckVl(vl, this);
    }

}
