package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraG extends PacketCheck {

    private int stage;
    
    public KillAuraG(PlayerData playerData) {
        super(playerData, "Kill-Aura (G)");
        stage = 0;
    }
    
    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraG") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        final int calculusStage = stage % 6;
        if (calculusStage == 0) {
            if (packet instanceof PacketPlayInArmAnimation) {
                ++stage;
            }
            else {
                stage = 0;
            }
        }
        else if (calculusStage == 1) {
            if (packet instanceof PacketPlayInUseEntity) {
                ++stage;
            }
            else {
                stage = 0;
            }
        }
        else if (calculusStage == 2) {
            if (packet instanceof PacketPlayInEntityAction) {
                ++stage;
            }
            else {
                stage = 0;
            }
        }
        else if (calculusStage == 3) {
            if (packet instanceof PacketPlayInFlying) {
                ++stage;
            }
            else {
                stage = 0;
            }
        }
        else if (calculusStage == 4) {
            if (packet instanceof PacketPlayInEntityAction) {
                ++stage;
            }
            else {
                stage = 0;
            }
        }
        else if (calculusStage == 5) {
            if (packet instanceof PacketPlayInFlying) {
                if (++stage >= 30 && alert(AlertType.RELEASE, player, String.format("S %.1f. VL %.1f/%s.", stage, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraG") && vl >= ConfigurationManager.banVL("KillAuraG")) {
                        punish(player);
                    }
                }
            }
            else {
                stage = 0;
            }
        }
        playerData.setCheckVl(vl, this);
    }
}
