package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraL extends PacketCheck {

    private boolean sent;

    public KillAuraL(PlayerData playerData) {
        super(playerData, "Kill-Aura (L)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraL") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInUseEntity && ((PacketPlayInUseEntity) packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            if (sent && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraL") && vl >= ConfigurationManager.banVL("KillAuraL")) {
                    punish(player);
                }
            }
        } else if (packet instanceof PacketPlayInEntityAction) {
            final PacketPlayInEntityAction.EnumPlayerAction action = ((PacketPlayInEntityAction) packet).b();

            if (action == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING || action == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING || action == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING || action == PacketPlayInEntityAction.EnumPlayerAction.STOP_SNEAKING) {
                sent = true;
            }
        } else if (packet instanceof PacketPlayInFlying) {
            sent = false;
        }
        playerData.setCheckVl(vl, this);
    }

}
