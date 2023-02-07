package org.crystalpvp.anticheat.check.combat.killaura;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class KillAuraC extends PacketCheck {

    private float lastYaw;

    public KillAuraC(PlayerData playerData) {
        super(playerData, "Kill-Aura (C)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("KillAuraC") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (playerData.getLastTarget() == null) {
            return;
        }

        if (packet instanceof PacketPlayInFlying) {
            final PacketPlayInFlying flying = (PacketPlayInFlying) packet;

            if (flying.h() && !playerData.isAllowTeleport()) {
                final CrystalLocation targetCrystalLocation = playerData.getLastPlayerPacket(playerData.getLastTarget(), MathUtil.pingFormula(playerData.getPing()));

                if (targetCrystalLocation == null) {
                    return;
                }

                final CrystalLocation playerCrystalLocation = playerData.getLastMovePacket();

                if (playerCrystalLocation.getX() == targetCrystalLocation.getX()) {
                    return;
                }

                if (targetCrystalLocation.getZ() == playerCrystalLocation.getZ()) {
                    return;
                }

                final float yaw = flying.d();

                if (yaw != lastYaw) {
                    final float bodyYaw = MathUtil.getDistanceBetweenAngles(yaw, MathUtil.getRotationFromPosition
                            (playerCrystalLocation, targetCrystalLocation)[0]);

                    if (bodyYaw == 0.0f && alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true)) {

                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("KillAuraC") && vl >= ConfigurationManager.banVL("KillAuraC")) {
                            punish(player);
                        }
                    }
                }

                lastYaw = yaw;
            }
            playerData.setCheckVl(vl, this);
        }
    }

}
