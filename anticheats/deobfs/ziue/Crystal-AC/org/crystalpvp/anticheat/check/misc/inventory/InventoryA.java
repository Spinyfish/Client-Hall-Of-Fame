package org.crystalpvp.anticheat.check.misc.inventory;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class InventoryA extends PacketCheck {
    public InventoryA(PlayerData playerData) {
        super(playerData, "InventoryClick (A)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("InventoryA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if ((packet instanceof PacketPlayInWindowClick && ((PacketPlayInWindowClick) packet).a() == 0 && !playerData.isInventoryOpen())
            || (packet instanceof PacketPlayInWindowClick && ((PacketPlayInWindowClick) packet).a() == 0 && playerData.isSprinting())
            || (packet instanceof PacketPlayInWindowClick && ((PacketPlayInWindowClick) packet).a() == 0 && playerData.isSneaking())) {

            if (alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("InventoryA") && vl >= ConfigurationManager.banVL("InventoryA")) {
                    punish(player);
                }
            }
            playerData.setInventoryOpen(true);
        }
        playerData.setCheckVl(vl, this);
    }

}
