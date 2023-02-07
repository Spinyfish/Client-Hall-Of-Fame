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

public class BadPacketsA extends PacketCheck {

    private int streak;

    public BadPacketsA(PlayerData playerData) {
        super(playerData, "Packets (A)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        if (packet instanceof PacketPlayInFlying) {
            if (((PacketPlayInFlying) packet).g()) {
                streak = 0;
            } else if (++streak > 20 && alert(AlertType.RELEASE, player, "", true)) {
                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsA") && vl >= ConfigurationManager.banVL("BadPacketsA")) {
                    punish(player);
                }
            }
        } else if (packet instanceof PacketPlayInSteerVehicle) {
            streak = 0;
        }
        playerData.setCheckVl(vl, this);
    }

}
