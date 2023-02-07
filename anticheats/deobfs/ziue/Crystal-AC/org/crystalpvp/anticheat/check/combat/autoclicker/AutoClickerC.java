package org.crystalpvp.anticheat.check.combat.autoclicker;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerC extends PacketCheck {

    private boolean sent;

    public AutoClickerC(PlayerData playerData) {
        super(playerData, "Auto-Clicker (C)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("AutoClickerC") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInBlockDig) {
            final PacketPlayInBlockDig.EnumPlayerDigType digType = ((PacketPlayInBlockDig) packet).c();

            if (digType == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                sent = true;
            } else if (digType == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {

                if (sent) {
                    if (++vl > 10 && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {
                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerC") && vl >= ConfigurationManager.banVL("AutoClickerC")) {
                            punish(player);
                        }
                    }
                } else {
                    vl = 0;
                }

                playerData.setCheckVl(vl, this);
            }
        } else if (packet instanceof PacketPlayInArmAnimation) {
            sent = false;
        }
        playerData.setCheckVl(vl, this);
    }

}
