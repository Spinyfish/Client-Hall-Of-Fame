package org.crystalpvp.anticheat.check.combat.aimassist;

import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.*;
import org.crystalpvp.anticheat.api.event.player.*;
import org.crystalpvp.anticheat.api.manager.*;
import org.crystalpvp.anticheat.api.update.*;
import org.crystalpvp.anticheat.api.util.*;
import org.crystalpvp.anticheat.data.player.*;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AimAssistH extends RotationCheck {

    public AimAssistH(PlayerData playerData) {
        super(playerData, "Aim (H)");
    }

    @Override
    public void handleCheck(final Player player, final RotationUpdate update) {
        if (!ConfigurationManager.isEnabled("AimAssistH")) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        if (System.currentTimeMillis() - this.playerData.getLastAttackPacket() > 10000L) {
            return;
        }

        final float diffYaw = MathUtil.getDistanceBetweenAngles(update.getTo().getYaw(), update.getFrom().getYaw());

        if (update.getFrom().getPitch() == update.getTo().getPitch() && diffYaw >= 3.0f && update.getFrom().getPitch() != 90.0f && update.getTo().getPitch() != 90.0f) {
            if ((vl += 0.9) >= 6.3) {
                this.alert(AlertType.EXPERIMENTAL, player, String.format("Y %.1f. VL %.1f.", diffYaw, vl), true);
                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AimAssistH") && vl >= ConfigurationManager.banVL("AimAssistH")) {
                    punish(player);
                }
            }
        } else {
            vl -= 1.6;
        }
        playerData.setCheckVl(vl, this);
    }
}
