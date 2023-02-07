package org.crystalpvp.anticheat.check.combat.autoclicker;

import net.minecraft.server.v1_8_R3.*;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerF extends PacketCheck {

    private boolean failed;
    private boolean sent;
    private double vl;

    public AutoClickerF(PlayerData playerData) {
        super(playerData, "Auto-Clicker (F)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("AutoClickerF") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);


        if (packet instanceof PacketPlayInBlockDig) {
            PacketPlayInBlockDig.EnumPlayerDigType digType = ((PacketPlayInBlockDig) packet).c();

            if (digType == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                this.sent = true;
            } else if (digType == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                 vl = (int) this.vl;

                if (this.sent ) {
                    if (++vl >= 10) {
                        vl = 0;
                        alert(AlertType.RELEASE, player, String.format("Sent %s. VL %.1f/%s.", sent, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerF") && vl >= ConfigurationManager.banVL("AutoClickerF")) {
                            punish(player);
                        }
                    }
                } else {
                    vl -= Math.min(vl + 4.0, 0.1);
                    vl = 0;
                }
                this.vl = vl;
            }
        } else if (packet instanceof PacketPlayInArmAnimation) {
            this.sent = false;
        }

        playerData.setCheckVl(vl, this);
    }

}
