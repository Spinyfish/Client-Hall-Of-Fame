package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraR extends PacketCheck {

    private boolean sentUseEntity;

    public KillAuraR(PlayerData playerData) {
        super(playerData, "Kill-Aura (R)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraR") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInBlockPlace) {
            if (((PacketPlayInBlockPlace) packet).getFace() != 255 && sentUseEntity && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraR") && vl >= ConfigurationManager.banVL("KillAuraR")) {
                    punish(player);
                }
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            sentUseEntity = true;
        } else if (packet instanceof PacketPlayInFlying) {
            sentUseEntity = false;
        }
        playerData.setCheckVl(vl, this);
    }

}
