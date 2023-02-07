package org.crystalpvp.anticheat.check.connection.badpackets;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BadPacketsI extends PacketCheck {

    private float lastYaw;
    private float lastPitch;
    private boolean ignore;

    public BadPacketsI(PlayerData playerData) {
        super(playerData, "Packets (I)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsI") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying) {
            final PacketPlayInFlying flying = (PacketPlayInFlying) packet;

            if (!flying.g() && flying.h()) {
                if (lastYaw == flying.d() && lastPitch == flying.e()) {
                    if (!ignore) {
                        alert(AlertType.EXPERIMENTAL, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsI") && vl >= ConfigurationManager.banVL("BadPacketsI")) {
                            punish(player);
                        }
                    }
                    ignore = false;
                }
                lastYaw = flying.d();
                lastPitch = flying.e();
            } else {
                ignore = true;
            }
        } else if (packet instanceof PacketPlayInSteerVehicle) {
            ignore = true;
        }
        playerData.setCheckVl(vl, this);
    }

}
