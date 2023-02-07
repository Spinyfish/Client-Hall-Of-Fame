package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraA extends PacketCheck {
    private boolean sent;

    public KillAuraA(PlayerData playerData) {
        super(playerData, "Kill-Aura (A)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraA") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.getPing() > 70) {
            return;
        }

        if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) packet).a() == PacketPlayInUseEntity
                .EnumEntityUseAction.ATTACK) {
            if (!sent) {
                if (alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraA") && vl >= ConfigurationManager.banVL("KillAuraA")) {
                        punish(player);
                    }
                }
            } else {
                sent = false;
            }
        } else if (packet instanceof PacketPlayInArmAnimation) {
            sent = true;
        } else if (packet instanceof PacketPlayInFlying) {
            sent = false;
        }
        playerData.setCheckVl(vl, this);
    }
}
