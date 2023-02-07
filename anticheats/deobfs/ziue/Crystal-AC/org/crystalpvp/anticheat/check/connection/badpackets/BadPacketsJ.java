package org.crystalpvp.anticheat.check.connection.badpackets;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BadPacketsJ extends PacketCheck {

    private boolean placing;

    public BadPacketsJ(PlayerData playerData) {
        super(playerData, "Packets/Others (J)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsJ") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInBlockDig) {
            if (((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM) {
                if (!placing && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsJ") && vl >= ConfigurationManager.banVL("BadPacketsJ")) {
                        punish(player);
                    }
                }
                placing = false;
            }
        } else if (packet instanceof PacketPlayInBlockPlace) {
            placing = true;
        }
        playerData.setCheckVl(vl, this);
    }

}
