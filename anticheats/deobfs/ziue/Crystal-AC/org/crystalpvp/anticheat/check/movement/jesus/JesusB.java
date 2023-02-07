package org.crystalpvp.anticheat.check.movement.jesus;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.BlockUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class JesusB extends PacketCheck {

    public JesusB(PlayerData playerData) {
        super(playerData, "Jesus (B)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("JesusB")) {
            return;
        }

        double vl = playerData.getCheckVl(this);


        if (packet instanceof PacketPlayInFlying && ((PacketPlayInFlying) packet).f() && BlockUtil.isOnPlatform(player.getLocation(), "WATER") && !player.isInsideVehicle() && player.getGameMode() != GameMode.CREATIVE && !player.isFlying()
                && !BlockUtil.isBlockingVelocityH(player.getLocation())) {


            if (++vl >= 10) {
                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("JesusB") && vl >= ConfigurationManager.banVL("JesusB")) {
                    punish(player);
                }
            }

        }



        playerData.setCheckVl(vl, this);


    }
}