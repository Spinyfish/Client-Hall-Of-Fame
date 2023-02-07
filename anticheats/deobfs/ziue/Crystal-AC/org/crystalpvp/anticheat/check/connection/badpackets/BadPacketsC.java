package org.crystalpvp.anticheat.check.connection.badpackets;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BadPacketsC extends PacketCheck {

    private boolean sent;

    public BadPacketsC(PlayerData playerData) {
        super(playerData, "Packets (C)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsC") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInEntityAction) {
            final PacketPlayInEntityAction.EnumPlayerAction playerAction = ((PacketPlayInEntityAction) packet).b();

            if (playerAction == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING || playerAction == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
                if (sent) {
                    if (alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsC") && vl >= ConfigurationManager.banVL("BadPacketsC")) {
                            punish(player);
                        }
                    }
                } else {
                    sent = true;
                }
                playerData.setCheckVl(vl, this);
            }
        } else if (packet instanceof PacketPlayInFlying) {
            sent = false;
        }
        playerData.setCheckVl(vl, this);
    }

}
