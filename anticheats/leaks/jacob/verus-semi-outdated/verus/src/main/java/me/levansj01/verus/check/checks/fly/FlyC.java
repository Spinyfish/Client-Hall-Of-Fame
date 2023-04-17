package me.levansj01.verus.check.checks.fly;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.task.ServerTickTask;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.MutableBlockLocation;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.location.IVector3d;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.World;

@CheckInfo(type=CheckType.FLY, subType="C", friendlyName="Fly", version=CheckVersion.RELEASE, minViolations=-1.0, maxViolations=50)
public class FlyC
        extends Check
        implements PacketHandler {
    private Double lastY = null;
    private int lastBypassTick = -10;
    private int threshold;

    public void handle(VPacketPlayInFlying<?> vPacketPlayInFlying) {
        if (this.lastY != null) {
            double d;
            if (vPacketPlayInFlying.isPos()) {
                d = vPacketPlayInFlying.getY();
            } else {
                d = this.lastY.doubleValue();
            }
            if (!(this.lastY != d || !(d > 0.0) || this.playerData.isVehicle() || vPacketPlayInFlying.isGround() || this.playerData.canFly() || !this.playerData.isSurvival() || this.playerData.isLevitating() || this.playerData.isFallFlying() || this.playerData.isGliding() || this.playerData.isTeleporting() || this.playerData.isTeleportingV2() || this.playerData.hasPlacedBucket() || this.playerData.hasPlacedBlock(true) || this.playerData.getTotalTicks() - 20 <= this.lastBypassTick || this.playerData.getVelocityTicks() <= this.playerData.getMaxPingTicks() || !this.playerData.isSpawned() || ServerTickTask.getInstance().isLagging(this.playerData.getTimestamp()))) {
                boolean bl = this.playerData.hasLag() || this.playerData.hasFast();
                PlayerLocation playerLocation = this.playerData.getLocation();
                World world = this.player.getWorld();
                Cuboid cuboid = new Cuboid((IVector3d)playerLocation).add(-0.5, 0.5, -0.5, 2.0, -0.5, 0.5);
                int n = this.playerData.getTotalTicks();
                this.run(() -> {
                    MutableBlockLocation mutableBlockLocation = playerLocation.toBlock();
                    boolean bl3 = mutableBlockLocation.isWaterLogged(world) || mutableBlockLocation.add(0, -1, 0).isWaterLogged(world) || mutableBlockLocation.add(0, 2, 0).isWaterLogged(world);
                    if (cuboid.checkBlocks(this.player, world, material -> {
                        return MaterialList.BAD_VELOCITY.contains(material) && !MaterialList.SHULKER_BOX.contains(material) && material != MaterialList.PURPLE_FUCKING_SHULKER;
                    }) && !bl) {
                        int n2;
                        if (bl) {
                            n2 = 4;
                        } else {
                            n2 = 1;
                        }
                        if (this.threshold++ > n2) {
                            if (bl) {
                                this.threshold = 0;
                            }
                            this.handleViolation(String.format((String)"Y: %.2f", (Object[])new Object[]{d}), this.threshold - 1);
                        }
                    } else {
                        this.threshold = 0;
                        this.decreaseVL(0.01);
                        this.lastBypassTick = n;
                    }
                });
            }
            this.run(() -> {
                this.threshold = 0;
                this.decreaseVL(0.01);
            });
        }
        if (vPacketPlayInFlying.isPos()) {
            this.lastY = vPacketPlayInFlying.getY();
        }
    }
}
