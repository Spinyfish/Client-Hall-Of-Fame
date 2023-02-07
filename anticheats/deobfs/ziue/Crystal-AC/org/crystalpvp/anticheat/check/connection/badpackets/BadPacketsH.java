package org.crystalpvp.anticheat.check.connection.badpackets;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BadPacketsH extends PacketCheck {

    private int lastSlot;

    public BadPacketsH(PlayerData playerData) {
        super(playerData, "Packets (H)");

        lastSlot = -1;
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsH") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInHeldItemSlot) {
            final int slot = ((PacketPlayInHeldItemSlot) packet).a();

            if (lastSlot == slot && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsH") && vl >= ConfigurationManager.banVL("BadPacketsH")) {
                    punish(player);
                }
            }

            lastSlot = slot;
        }
        playerData.setCheckVl(vl, this);
    }

}
