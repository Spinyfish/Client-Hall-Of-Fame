package org.crystalpvp.anticheat.check.connection.badpackets;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BadPacketsG extends PacketCheck {

    private PacketPlayInEntityAction.EnumPlayerAction lastAction;

    public BadPacketsG(PlayerData playerData) {
        super(playerData, "Packets (G)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsG") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInEntityAction) {
            final PacketPlayInEntityAction.EnumPlayerAction playerAction = ((PacketPlayInEntityAction) packet).b();

            if (playerAction == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING || playerAction == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
                if (lastAction == playerAction && playerData.getLastAttackPacket() + 10000L > System.currentTimeMillis() && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsG") && vl >= ConfigurationManager.banVL("BadPacketsG")) {
                        punish(player);
                    }
                }

                lastAction = playerAction;
            }
        }
        playerData.setCheckVl(vl, this);
    }

}
