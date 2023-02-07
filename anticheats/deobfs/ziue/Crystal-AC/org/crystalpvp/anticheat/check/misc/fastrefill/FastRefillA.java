package org.crystalpvp.anticheat.check.misc.fastrefill;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FastRefillA extends PacketCheck {

    private int beforeLastSlot, lastSlot, duplicateRefill;
    private long diffClick;

    public FastRefillA(PlayerData playerData) {
        super(playerData, "FastRefill (A)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("FastRefillA")) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInWindowClick) {

            diffClick = TimesUtil.differenceTimeMillis(playerData.getWindowClick(), System.currentTimeMillis());
            lastSlot = playerData.getLastSlotClicked();

            if (playerData.getLastSlotType() != null && playerData.getLastSlotType().equalsIgnoreCase("CONTAINER")) {

                if (lastSlot == beforeLastSlot + 1 && diffClick < 50
                        && (playerData.getLastItemSlotClicked().equalsIgnoreCase("POTION(37)")
                        || playerData.getLastItemSlotClicked().equalsIgnoreCase("POTION(69)")
                        || playerData.getLastItemSlotClicked().equalsIgnoreCase("MUSHROOM_SOUP(0)"))) {

                    if (duplicateRefill > 2) {

                        if (++vl > 5) {
                            alert(AlertType.RELEASE, player, String.format("Delay %s. Refill %s. VL %.1f/%s", diffClick, duplicateRefill, ++vl, ConfigurationManager.alertVl(getClass().getSimpleName())), false);

                            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FastRefillA") && vl >= ConfigurationManager.banVL("FastRefillA")) {
                                punish(player);
                            }
                        }
                    }

                    ++duplicateRefill;


                } else {
                    duplicateRefill = 0;
                    vl -= 0.2;
                }
            }
        }


        beforeLastSlot = lastSlot;
        playerData.setCheckVl(vl, this);
    }

}