package org.crystalpvp.anticheat.check.misc.fastbreak;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FastBreakA extends PacketCheck {
    private int stage = 0;

    public FastBreakA(PlayerData playerData) {
        super(playerData, "FastBreak (A)");
    }


    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("FastBreakA")) {
            return;
        }


        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying) {
            if (stage == 2) {
                vl -= Math.min(vl + 1.0, 0.01);
            }
            stage = stage == 1 ? 2 : 0;
        } else if (packet instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig packetPlayInBlockDig = (PacketPlayInBlockDig)packet;
            if (packetPlayInBlockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                stage = 1;
            } else if (packetPlayInBlockDig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK && stage == 2) {

                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s", ++vl, ConfigurationManager.alertVl(getClass().getSimpleName())), false);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BlinkA") && vl >= ConfigurationManager.banVL("BlinkA")) {
                    punish(player);
                }

                stage = 0;
            }
        }
    }
}