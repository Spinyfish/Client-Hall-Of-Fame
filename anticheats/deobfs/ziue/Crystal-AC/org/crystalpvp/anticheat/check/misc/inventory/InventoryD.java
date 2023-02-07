package org.crystalpvp.anticheat.check.misc.inventory;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */


public class InventoryD extends PacketCheck {

    private boolean invOpen;
    private boolean invWasOpen;
    private boolean sprintNoInv;
    private boolean sneakNoInv;

    public InventoryD(PlayerData playerData) {
        super(playerData, "Inventory (D)");
    }


    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("InventoryD")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        invOpen = playerData.isInventoryOpen();

        if (sprintNoInv && invOpen && !invWasOpen && !playerData.isSprinting()) {

            if (alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("InventoryD") && vl >= ConfigurationManager.banVL("InventoryD")) {
                    punish(player);
                }
            }

        }

        if (sneakNoInv && invOpen && !invWasOpen && !playerData.isSneaking()) {

            if (alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("InventoryD") && vl >= ConfigurationManager.banVL("InventoryD")) {
                    punish(player);
                }
            }

        }




        if (!invOpen && playerData.isSprinting()) {
            sprintNoInv = true;
        } else {
            sprintNoInv = false;
        }

        if (!invOpen && playerData.isSneaking()) {
            sneakNoInv = true;
        } else {
            sneakNoInv = false;
        }



        invWasOpen = invOpen;
        playerData.setCheckVl(vl, this);
    }
}
