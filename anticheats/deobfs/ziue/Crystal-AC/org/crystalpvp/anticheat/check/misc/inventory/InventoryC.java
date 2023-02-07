package org.crystalpvp.anticheat.check.misc.inventory;


import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.LinkedList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class InventoryC extends PacketCheck {

    private final Deque<Long> delays;

    public InventoryC(PlayerData playerData) {
        super(playerData, "Inventory (C)");

        delays = new LinkedList<>();
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("InventoryC")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInWindowClick && System.currentTimeMillis() - playerData.getLastDelayedMovePacket() > 220L && !playerData.isAllowTeleport()) {
            final CrystalLocation lastMovePacket = playerData.getLastMovePacket();

            if (lastMovePacket == null) {
                return;
            }

            final long delay = System.currentTimeMillis() - lastMovePacket.getTimestamp();

            delays.add(delay);

            if (delays.size() == 10) {
                double average = 0.0;

                for (final long loopDelay : delays) {
                    average += loopDelay;
                }

                average /= delays.size();

                delays.clear();

                if (average <= 35.0) {
                    if ((vl += 1.25) >= 4.0) {
                        if (alert(AlertType.RELEASE, player, String.format("AVG %.1f. VL %.1f/%s.", average, vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("InventoryC") && vl >= ConfigurationManager.banVL("InventoryC")) {
                                punish(player);
                            }
                        } else {
                            vl = 0.0;
                        }
                    }
                } else {
                    vl -= 0.5;
                }
            }
        }
        playerData.setCheckVl(vl, this);
    }

}
