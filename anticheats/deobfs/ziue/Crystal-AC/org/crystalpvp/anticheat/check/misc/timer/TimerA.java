package org.crystalpvp.anticheat.check.misc.timer;


import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.LinkedList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class TimerA extends PacketCheck {

    private final Deque<Long> delays;
    private long lastPacketTime;

    public TimerA(PlayerData playerData) {
        super(playerData, "Timer (A)");
        delays = new LinkedList<>();
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("TimerA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying && !playerData.isAllowTeleport() && System.currentTimeMillis()
                - playerData.getLastDelayedMovePacket() > 220L) {
            delays.add(System.currentTimeMillis() - lastPacketTime);
            if (delays.size() == 50) {
                double average = 0.0;
                for (final long l : delays) {
                    average += l;
                }
                average /= delays.size();
                if (average <= 48.0) {
                    if ((vl += 1.25) >= 4.0) {
                        alert(AlertType.RELEASE, player, String.format("AVG %.3f. R %.2f. VL %.1f/%s.", average, 50.0 / average, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                    }

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("TimerA") && vl >= ConfigurationManager.banVL("TimerA")) {
                        punish(player);
                    }
                } else {
                    vl -= 0.5;
                }
                playerData.setCheckVl(vl, this);
                delays.clear();
            }
            lastPacketTime = System.currentTimeMillis();
        }
        playerData.setCheckVl(vl, this);
    }

}
