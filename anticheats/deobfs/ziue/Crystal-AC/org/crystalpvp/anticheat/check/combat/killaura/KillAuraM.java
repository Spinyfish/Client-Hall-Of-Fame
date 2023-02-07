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

public class KillAuraM extends PacketCheck {

    private int swings;
    private int attacks;

    public KillAuraM(PlayerData playerData) {
        super(playerData, "Kill-Aura (M)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraM") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (!playerData.isDigging() && !playerData.isPlacing()) {
            if (packet instanceof PacketPlayInFlying) {
                if (attacks > 0 && swings > attacks) {
                    alert(AlertType.EXPERIMENTAL, player, String.format("Swings %s. Attacks %s. VL %.1f/%s.", swings, attacks, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraM") && vl >= ConfigurationManager.banVL("KillAuraM")) {
                        punish(player);
                    }
                }

                final KillAuraN auraN = playerData.getCheck(KillAuraN.class);

                if (auraN != null) {
                    auraN.handleCheck(player, new int[]{swings, attacks});
                }

                swings = 0;
                attacks = 0;
            } else if (packet instanceof PacketPlayInArmAnimation) {
                ++swings;
            } else if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                ++attacks;
            }
        }
        playerData.setCheckVl(vl, this);
    }

}
