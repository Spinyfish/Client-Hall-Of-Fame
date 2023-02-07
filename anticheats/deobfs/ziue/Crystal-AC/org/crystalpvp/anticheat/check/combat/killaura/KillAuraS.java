package org.crystalpvp.anticheat.check.combat.killaura;

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

public class KillAuraS extends PacketCheck {

    private boolean sentArmAnimation;
    private boolean sentAttack;
    private boolean sentBlockPlace;
    private boolean sentUseEntity;

    public KillAuraS(PlayerData playerData) {
        super(playerData, "Kill-Aura (S)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraS") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInArmAnimation) {
            this.sentArmAnimation = true;
        } else if (packet instanceof PacketPlayInUseEntity) {
            if (((PacketPlayInUseEntity) packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                this.sentAttack = true;
            } else {
                this.sentUseEntity = true;
            }
        } else if (packet instanceof PacketPlayInBlockPlace && ((PacketPlayInBlockPlace) packet).getItemStack() != null && ((PacketPlayInBlockPlace) packet).getItemStack().getName().toLowerCase().contains("sword")) {
            this.sentBlockPlace = true;
        } else if (packet instanceof PacketPlayInFlying) {
            if (this.sentArmAnimation && !this.sentAttack && this.sentBlockPlace && this.sentUseEntity && alert(AlertType.EXPERIMENTAL, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                if(!playerData.isBanned() && !ConfigurationManager.isAutoBanning("KillAuraS") && vl > 2) {
                    punish(player);
                }
            }
            this.sentUseEntity = false;
            this.sentBlockPlace = false;
            this.sentAttack = false;
            this.sentArmAnimation = false;
        }
        playerData.setCheckVl(vl, this);
    }
}
