package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraH extends PacketCheck {

    private boolean sent;

    public KillAuraH(PlayerData playerData) {
        super(playerData, "Kill-Aura (H)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraH") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInBlockDig) {
            final PacketPlayInBlockDig.EnumPlayerDigType digType = ((PacketPlayInBlockDig) packet).c();

            if ((digType == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK || digType == PacketPlayInBlockDig.EnumPlayerDigType.RELEASE_USE_ITEM) && sent && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraH") && vl >= ConfigurationManager.banVL("KillAuraH")) {
                    punish(player);
                }
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            sent = true;
        } else if (packet instanceof PacketPlayInFlying) {
            sent = false;
        }
        playerData.setCheckVl(vl, this);
    }

}
