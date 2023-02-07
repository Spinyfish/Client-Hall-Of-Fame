package org.crystalpvp.anticheat.check.misc.inventory;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class InventoryB extends PacketCheck {

    boolean inventoryMove;

    public InventoryB(PlayerData playerData) {
        super(playerData, "Inventory/MoreCarry (B)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("InventoryB")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (((packet instanceof PacketPlayInEntityAction && ((PacketPlayInEntityAction) packet).b() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING) && playerData.isInventoryOpen())
                || ((packet instanceof PacketPlayInEntityAction && ((PacketPlayInEntityAction) packet).b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING) && playerData.isInventoryOpen())
                || ((packet instanceof PacketPlayInArmAnimation) && playerData.isInventoryOpen())
                || (inventoryMove && playerData.isSprinting())
                || (inventoryMove && playerData.isSneaking())
                && playerData.getPing() < 300) {

            if (inventoryMove) {

                inventoryMove = false;

                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("InventoryB") && vl >= ConfigurationManager.banVL("InventoryB")) {
                    punish(player);
                }

            } else {
                inventoryMove = true;
            }

            playerData.setInventoryOpen(false);
        } else {
            inventoryMove = false;
        }

        playerData.setCheckVl(vl, this);
    }

}
