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

public class KillAuraQ extends PacketCheck {

    private boolean sentAttack;
    private boolean sentInteract;

    public KillAuraQ(PlayerData playerData) {
        super(playerData, "Kill-Aura (Q)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraQ") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInBlockPlace) {
            if (sentAttack && !sentInteract && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraQ") && vl >= ConfigurationManager.banVL("KillAuraQ")) {
                    punish(player);
                }
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity.EnumEntityUseAction action = ((PacketPlayInUseEntity) packet).a();

            if (action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                sentAttack = true;
            } else if (action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                sentInteract = true;
            }
        } else if (packet instanceof PacketPlayInFlying) {
            final boolean b = false;
            sentInteract = b;
            sentAttack = b;
        }
        playerData.setCheckVl(vl, this);
    }

}
