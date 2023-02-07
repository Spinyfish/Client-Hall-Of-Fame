package org.crystalpvp.anticheat.check.combat.autoclicker;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AutoClickerB extends PacketCheck {

    private int swings;
    private int movements;

    public AutoClickerB(PlayerData playerData) {
        super(playerData, "Auto-Clicker (B)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("AutoClickerB") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInArmAnimation
                && !playerData.isDigging() && playerData.isPlacing() && !playerData.isFakeDigging()
                && (System.currentTimeMillis() - playerData.getLastDelayedMovePacket()) > 220L
                && (System.currentTimeMillis() - playerData.getLastMovePacket().getTimestamp()) < 110L) {
            ++swings;
        } else if (packet instanceof PacketPlayInFlying && ++movements == 20) {
            if (swings > ConfigurationManager.getInfoInt("AutoClickerB", "maxCps")) {
                alert(AlertType.RELEASE, player, String.format("BlocksPlace %s. VL %.1f/%s.", swings, ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("AutoClickerB") && vl >= ConfigurationManager.banVL("AutoClickerB")) {
                    punish(player);
                }
            }

            playerData.setLastBlocksPlace(swings);
            movements = swings = 0;
            playerData.setCheckVl(vl, this);
        }
    }

}
