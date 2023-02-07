package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraI extends PacketCheck {

    private boolean sent;

    public KillAuraI(PlayerData playerData) {
        super(playerData, "Kill-Aura (I)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraI") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInBlockDig && ((PacketPlayInBlockDig) packet).c() == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
            if (sent) {
                alert(AlertType.EXPERIMENTAL, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraI") && vl >= ConfigurationManager.banVL("KillAuraI")) {
                    punish(player);
                }
            }
        } else if (packet instanceof PacketPlayInArmAnimation) {
            sent = true;
        } else if (packet instanceof PacketPlayInFlying) {
            sent = false;
        }
        playerData.setCheckVl(vl, this);
    }

}
