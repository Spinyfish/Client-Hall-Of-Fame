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

public class KillAuraK extends PacketCheck {

    private int ticksSinceStage;
    private int streak;
    private int stage;

    public KillAuraK(PlayerData playerData) {
        super(playerData, "Kill-Aura (K)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraK") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInArmAnimation) {
            if (stage == 0) {
                stage = 1;
            } else {
                final boolean b = false;
                stage = (b ? 1 : 0);
                streak = (b ? 1 : 0);
            }
        } else if (packet instanceof PacketPlayInUseEntity) {
            if (stage == 1) {
                ++stage;
            } else {
                stage = 0;
            }
        } else if (packet instanceof PacketPlayInFlying.PacketPlayInPositionLook) {
            if (stage == 2) {
                ++stage;
            } else {
                stage = 0;
            }
        } else if (packet instanceof PacketPlayInFlying.PacketPlayInPosition) {
            if (stage == 3) {
                if (++streak >= 15) {
                    alert(AlertType.EXPERIMENTAL, player, String.format("STG %.1f. VL %.1f/%s.", stage, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraK") && vl >= ConfigurationManager.banVL("KillAuraK")) {
                        punish(player);
                    }
                }

                ticksSinceStage = 0;
            }

            stage = 0;
        }

        if (packet instanceof PacketPlayInFlying && ++ticksSinceStage > 40) {
            streak = 0;
        }
        playerData.setCheckVl(vl, this);
    }

}
