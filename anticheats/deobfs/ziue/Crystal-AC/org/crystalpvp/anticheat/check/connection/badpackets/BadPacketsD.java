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

public class BadPacketsD extends PacketCheck {

    private boolean sent;

    public BadPacketsD(PlayerData playerData) {
        super(playerData, "Packets (D)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsD") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.getPing() > 70) {
            return;
        }


        if (packet instanceof PacketPlayInEntityAction) {
            final PacketPlayInEntityAction.EnumPlayerAction playerAction = ((PacketPlayInEntityAction) packet).b();
            if (playerAction == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING || playerAction ==
                    PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING) {
                if (sent) {
                    alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsD") && vl >= ConfigurationManager.banVL("BadPacketsD")) {
                        punish(player);
                    }
                } else {
                    sent = true;
                }
            }
        } else if (packet instanceof PacketPlayInFlying) {
            sent = false;
        }
        playerData.setCheckVl(vl, this);
    }

}
