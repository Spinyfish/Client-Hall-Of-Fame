package org.crystalpvp.anticheat.check.connection.badpackets;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BadPacketsB extends PacketCheck {

    public BadPacketsB(PlayerData playerData) {
        super(playerData, "Packets (B)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsB") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        if (packet instanceof PacketPlayInFlying && Math.abs(((PacketPlayInFlying) packet).e()) > 90.0f && alert(AlertType.RELEASE, player, "", false)) {
            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsB") && vl >= ConfigurationManager.banVL("BadPacketsB")) {
                punish(player);
            }
        }
        playerData.setCheckVl(vl, this);
    }

}
