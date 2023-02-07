package org.crystalpvp.anticheat.check.combat.autoclicker;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerM extends PacketCheck {

    public AutoClickerM(PlayerData playerData) {
        super(playerData, "Auto-Clicker (M)");
    }

    private int swings;
    private int attacks;

    @Override
    public void handleCheck(Player player, Packet packet) {

        if (ConfigurationManager.isEnabled("AutoClickerM") == false) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        if (!playerData.isDigging() && !playerData.isPlacing()) {
            if (packet instanceof PacketPlayInFlying) {
                if (attacks > 0 && swings > attacks) {
                    alert(AlertType.EXPERIMENTAL, player, String.format("Swings %s. Attacks %s. VL %.1f/%s.", swings, attacks, ++vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerM") && vl >= ConfigurationManager.banVL("AutoClickerM")) {
                        punish(player);
                    }
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
