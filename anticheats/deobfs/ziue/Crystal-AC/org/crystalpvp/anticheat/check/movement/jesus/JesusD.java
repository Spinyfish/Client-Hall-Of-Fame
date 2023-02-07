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

public class JesusD extends PacketCheck {

    public JesusD(PlayerData playerData) {
        super(playerData, "Jesus (D)");
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!ConfigurationManager.isEnabled("JesusD")) {
            return;
        }

        double vl = playerData.getCheckVl(this);


        if (packet instanceof PacketPlayInFlying && ((PacketPlayInFlying) packet).f() && BlockUtil.isOnPlatform(player.getLocation(), "LAVA") && !player.isInsideVehicle() && player.getGameMode() != GameMode.CREATIVE && !player.isFlying()
                && !BlockUtil.isBlockingVelocityH(player.getLocation())) {


            if (++vl >= 10) {
                alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("JesusD") && vl >= ConfigurationManager.banVL("JesusD")) {
                    punish(player);
                }
            }

        }

        playerData.setCheckVl(vl, this);

    }
}