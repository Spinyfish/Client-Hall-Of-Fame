package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraD extends PacketCheck {

    public KillAuraD(PlayerData playerData) {
        super(playerData, "Kill-Aura (D)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraD") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) packet).a() == PacketPlayInUseEntity
                .EnumEntityUseAction.ATTACK && playerData.isPlacing() && alert(AlertType.RELEASE, player,String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

            if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraD") && vl >= ConfigurationManager.banVL("KillAuraD")) {
                punish(player);
            }
        }
        playerData.setCheckVl(vl, this);
    }

}
