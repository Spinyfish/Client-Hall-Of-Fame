package org.crystalpvp.anticheat.check.movement.fly;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.checks.PositionCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.update.PositionUpdate;
import org.crystalpvp.anticheat.api.util.Cuboid;
import org.crystalpvp.anticheat.api.util.MaterialList;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class FlyK extends PacketCheck {

    private Double lastY;
    private int threshold;
    private int lastBypassTick;

    public FlyK(PlayerData playerData) {
        super(playerData, "Fly (K)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (!ConfigurationManager.isEnabled("FlyK")) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInFlying) {
            final PacketPlayInFlying packetPlayInFlying = (PacketPlayInFlying) packet;
            if (this.lastY != null) {
                final double d;
                final double y = d = (packetPlayInFlying.g() ? packetPlayInFlying.b() : this.lastY);
                if (this.lastY == y && player.getVehicle() == null && !packetPlayInFlying.f() && !player.isFlying() && playerData.getTotalTicks() - 10 > this.lastBypassTick) {
                    final World world = player.getWorld();
                    final Cuboid cuboid = new Cuboid(-0.5, 0.5, 0.0, 1.5, -0.5, 0.5);
                    final int totalTicks = playerData.getTotalTicks();
                    final Cuboid cuboid2 = null;
                    final World world2 = null;
                    final double n;
                    final int lastBypassTick = 0;
                    if (this.threshold++ > 1) {
                        alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", vl, ConfigurationManager.alertVl(getClass().getSimpleName())), true);

                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("FlyK") && vl >= ConfigurationManager.banVL("FlyK")) {
                            punish(player);
                        }
                    } else {
                        this.threshold = 0;
                        vl -= Math.min(vl + 1.5, 0.01);
                        this.lastBypassTick = lastBypassTick;
                    }
                    return;
                } else {
                    this.threshold = 0;
                    vl -= Math.min(vl + 1.5, 0.01);
                    return;
                }
            }
            if (packetPlayInFlying.g()) {
                this.lastY = packetPlayInFlying.b();
            }
        }
        playerData.setCheckVl(vl, this);
    }
}
