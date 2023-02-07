package org.crystalpvp.anticheat.check.misc.timer;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class TimerC extends PacketCheck {

    private int packets;
    private double delayedPackets;

    public TimerC(PlayerData playerData) {
        super(playerData, "Timer/Speed (C)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("TimerC") == false) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        delayedPackets = playerData.getPing()/50;
        if (delayedPackets > 0.0 && delayedPackets < 1) {
            delayedPackets = 1;
        }


        if (packet instanceof PacketPlayInFlying) {
            ++packets;
        } else if (packet instanceof PacketPlayInKeepAlive) {
            if (packets > 45 + delayedPackets) {
                packets -= (45 + delayedPackets);

                if (++vl > 5) {
                    alert(AlertType.RELEASE, player, String.format("MorePackets %s. DelayedPackets %.1f. VL %.1f/%s.", packets, delayedPackets, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                }

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("TimerC") && vl >= ConfigurationManager.banVL("TimerC")) {
                    punish(player);
                }
            } else {
                vl -= 0.25;
            }

            packets = 0;
        }

        playerData.setCheckVl(vl, this);
    }

}

