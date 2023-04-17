package me.levansj01.verus.check.checks.fly;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.MathUtil;
import org.bukkit.World;

@CheckInfo(
        type = CheckType.FLY,
        subType = "D",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -5.0,
        maxViolations = 25
)
public class FlyD extends Check implements PacketHandler {
    private int lastBypassTick = -10;
    private Double lastY = null;
    private int jumped;
    private boolean jumping = false;

    public void handle(VPacketPlayInFlying vPacket) {
        if (!vPacket.isGround() && this.playerData.getVelocityTicks() > this.playerData.getPingTicks() * 2
                && this.playerData.getTeleportTicks() > this.playerData.getPingTicks()
                && !this.playerData.isTeleportingV2() && !this.playerData.isFlying()
                && this.playerData.isSurvival() && this.playerData.getTotalTicks() - 40 > this.lastBypassTick
                && !this.playerData.hasPlacedBucket() && !this.playerData.isFallFlying()
                && !this.playerData.isGliding() && !this.playerData.isRiptiding()
                && !this.playerData.isLevitating() && !this.playerData.isVehicle()) {
            if (this.lastY != null) {
                if (this.jumping && vPacket.getY() < this.lastY) {
                    if (this.jumped++ > 1 && !this.playerData.hadJumpBoost()) {
                        World world = this.player.getWorld();
                        Cuboid cuboid1 = Cuboid.withLimit(this.playerData.getLastLocation(), this.playerData.getLocation(), 16)
                                .add(-0.5, 0.5, -0.5, 2.0, -0.5, 0.5);
                        Cuboid cuboid2 = Cuboid.withLimit(this.playerData.getLastLocation(), this.playerData.getLocation(), 16)
                                .add(-0.5, 0.5, -2.0, 0.0, -0.5, 0.5);
                        int totalTicks = this.playerData.getTotalTicks();
                        this.run(() -> {
                            boolean waterLogged = this.playerData.getLocation().toBlock().isWaterLogged(world);
                            ClientVersion clientVersion = this.playerData.getVersion();
                            ServerVersion serverVersion = NMSManager.getInstance().getServerVersion();
                            if (!waterLogged && cuboid1.checkBlocks(this.player, world, material -> !MaterialList.BAD_VELOCITY.contains(material))
                                    && (clientVersion.before(ClientVersion.V1_13) || serverVersion.before(ServerVersion.v1_13_R2)
                                    || cuboid2.checkBlocks(this.player, world, material -> !MaterialList.BOBBLE.contains(material)))) {
                                this.handleViolation("", (this.jumped - 1));
                            } else {
                                this.jumped = 0;
                                this.violations -= Math.min(this.violations + 1.0, 0.05);
                                this.lastBypassTick = totalTicks;
                            }

                        });
                    }

                    this.jumping = false;
                } else if (vPacket.getY() > this.lastY) {
                    this.jumping = true;
                }
            }
        } else if (MathUtil.onGround(this.playerData.getLocation().getY()) || (this.playerData.getLocation().getY() - 0.41999998688697815) % 1.0 > 1.0E-15) {
            this.jumped = 0;
            this.jumping = false;
        }

        this.violations -= Math.min(this.violations + 5.0, 0.025);
        if (vPacket.isPos()) {
            this.lastY = vPacket.getY();
        }

    }

}
