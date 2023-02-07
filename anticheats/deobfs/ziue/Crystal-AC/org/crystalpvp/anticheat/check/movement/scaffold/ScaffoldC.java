package org.crystalpvp.anticheat.check.movement.scaffold;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class ScaffoldC extends PacketCheck {

    private int looks;
    private int stage;
    
    public ScaffoldC(PlayerData playerData) {
        super(playerData, "Scaffold (C)");
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("ScaffoldC")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying.PacketPlayInLook) {
            if (stage == 0) {
                ++stage;
            } else if (stage == 4) {
                if ((vl += 1.75) > 3.5) {
                    alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                }

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("ScaffoldC") && vl >= ConfigurationManager.banVL("ScaffoldC")) {
                    punish(player);
                }

                stage = 0;
            } else {
                final boolean b = false;
                looks = (b ? 1 : 0);
                stage = (b ? 1 : 0);
                vl -= 0.2;
            }
        } else if (packet instanceof PacketPlayInBlockPlace) {
            if (stage == 1) {
                ++stage;
            } else {
                final boolean b2 = false;
                looks = (b2 ? 1 : 0);
                stage = (b2 ? 1 : 0);
            }
        } else if (packet instanceof PacketPlayInArmAnimation) {
            if (stage == 2) {
                ++stage;
            } else {
                final boolean b3 = false;
                looks = (b3 ? 1 : 0);
                stage = (b3 ? 1 : 0);
                vl -= 0.2;
            }
        } else if (packet instanceof PacketPlayInFlying.PacketPlayInPositionLook || packet instanceof PacketPlayInFlying.PacketPlayInPosition) {
            if (stage == 3) {
                if (++looks == 3) {
                    stage = 4;
                    looks = 0;
                }
            } else {
                final boolean b4 = false;
                looks = (b4 ? 1 : 0);
                stage = (b4 ? 1 : 0);
            }
        }

        playerData.setCheckVl(vl, this);
    }

}
