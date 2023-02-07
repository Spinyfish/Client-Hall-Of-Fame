package org.crystalpvp.anticheat.check.movement.blink;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BlinkA extends PacketCheck {

    private long diffMove;

    public BlinkA(PlayerData playerData) {
        super(playerData, "Blink/FreeCam (A)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("BlinkA")) {
            return;
        }

        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInKeepAlive && !player.isInsideVehicle() &&!player.isDead()) {

            diffMove = TimesUtil.differenceTimeSecond(playerData.getLastMove(), System.currentTimeMillis());

            if (diffMove > 0.8 && playerData.getPing() < 800) {

                alert(AlertType.EXPERIMENTAL, player, String.format("Delay %s. VL %.1f/%s", diffMove, ++vl, ConfigurationManager.alertVl(getClass().getSimpleName())), false);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BlinkA") && vl >= ConfigurationManager.banVL("BlinkA")) {
                    punish(player);
                }
            }

        }
        playerData.setCheckVl(vl, this);
    }

}

