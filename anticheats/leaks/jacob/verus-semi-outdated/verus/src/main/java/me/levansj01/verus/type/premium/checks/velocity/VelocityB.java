package me.levansj01.verus.type.premium.checks.velocity;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.util.Vector;

@CheckInfo(
        type = CheckType.VELOCITY,
        subType = "B",
        friendlyName = "hVelocity",
        version = CheckVersion.RELEASE,
        minViolations = -2.5D
)
public class VelocityB extends MovementCheck {
    private double total;
    private int lastBypassTicks;

    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        if (this.playerData.getLastVelX() != 0.0D && this.playerData.getLastVelZ() != 0.0D && this.playerData.getHorizontalVelocityTicks() > this.playerData.getMoveTicks()) {
            if (playerLocation2.getY() - playerLocation.getY() > 0.0D) {
                double xz = MathUtil.hypot(this.playerData.getLastVelX(), this.playerData.getLastVelZ());
                if (this.playerData.getLastLastLocation().getGround() && playerLocation.getGround() && MathUtil.onGround(playerLocation.getY()) && !playerLocation2.getGround() && !MathUtil.onGround(playerLocation2.getY())) {
                    if (this.playerData.getTotalTicks() - this.lastBypassTicks > 10 && xz > 0.25D) {
                        PlayerLocation playerLocation3;
                        if (this.playerData.getLastNonMoveTicks() == 0) {
                            Vector vector = new Vector(this.playerData.getLastLastLocation().getX(), this.playerData.getLastLastLocation().getY(), this.playerData.getLastLastLocation().getZ());
                            vector.subtract(new Vector(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()));
                            playerLocation3 = playerLocation2.add(vector.getX(), vector.getY(), vector.getZ());
                        } else {
                            playerLocation3 = playerLocation2.clone();
                        }
                        double xz2 = MathUtil.hypot(playerLocation2.getX() - playerLocation.getX(), playerLocation2.getZ() - playerLocation.getZ()),
                                xz3 = MathUtil.hypot(playerLocation3.getX() - playerLocation.getX(), playerLocation3.getZ() - playerLocation.getZ()),
                                xz4 = Math.max(xz2, xz3) / xz;
                        this.decreaseVL(0.0025D);
                        if (xz4 < 1.0D) {
                            this.run(() -> {
                                if ((new Cuboid(playerLocation2)).add(-1.0D, 1.0D, 0.0D, 2.05D, -1.0D, 1.0D).checkBlocks(this.player, this.player.getWorld(), (w) -> w == MaterialList.AIR)) {
                                    if ((this.total += 1.05D - xz4) > 2.0D) {
                                        this.total = 0.0D;
                                        this.handleViolation(String.format("P: %.2f D: %.3f", xz4, xz3));
                                    }
                                } else {
                                    this.total -= Math.min(this.total, 1.05D);
                                    this.lastBypassTicks = this.playerData.getTotalTicks();
                                }
                            });
                        } else {
                            this.total -= Math.min(this.total, 1.05D);
                        }
                    }
                }
                this.playerData.setLastVelX(0.0D);
                this.playerData.setLastVelZ(0.0D);
            }
        }
    }
}
