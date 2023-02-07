package org.crystalpvp.anticheat.check.movement.scaffold;


import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class ScaffoldA extends PacketCheck {

    private BlockPosition lastBlock;
    private float lastYaw;
    private float lastPitch;
    private float lastX;
    private float lastY;
    private float lastZ;

    public ScaffoldA(PlayerData playerData) {
        super(playerData, "Scaffold (A)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("ScaffoldA")) {
            return;
        }
        double vl = playerData.getCheckVl(this);
        if (packet instanceof PacketPlayInBlockPlace) {
            final PacketPlayInBlockPlace blockPlace = (PacketPlayInBlockPlace) packet;
            final BlockPosition blockPosition = blockPlace.a();
            final float x = blockPlace.d();
            final float y = blockPlace.e();
            final float z = blockPlace.f();

            if (lastBlock != null && (blockPosition.getX() != lastBlock.getX() || blockPosition.getY() != lastBlock.getY() || blockPosition.getZ() != lastBlock.getZ())) {
                final CrystalLocation crystalLocation = playerData.getLastMovePacket();

                if (lastX == x && lastY == y && lastZ == z) {
                    final float deltaAngle = Math.abs(lastYaw - crystalLocation.getYaw()) + Math.abs(lastPitch - crystalLocation.getPitch());

                    if (deltaAngle > 4.0f && ++vl >= 4.0) {
                        alert(AlertType.RELEASE, player, String.format("X %.1f. Y %.1f. Z %.1f. DAGL %.1f. VL %.1f/%s", x, y, z, deltaAngle, vl, ConfigurationManager.alertVl(getClass().getSimpleName())), false);
                    }

                    if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("ScaffoldA") && vl >= ConfigurationManager.banVL("ScaffoldA")) {
                        punish(player);
                    }
                } else {
                    vl -= 0.5;
                }

                playerData.setCheckVl(vl, this);

                lastX = x;
                lastY = y;
                lastZ = z;
                lastYaw = crystalLocation.getYaw();
                lastPitch = crystalLocation.getPitch();
            }

            lastBlock = blockPosition;
        }
        playerData.setCheckVl(vl, this);
    }

}
