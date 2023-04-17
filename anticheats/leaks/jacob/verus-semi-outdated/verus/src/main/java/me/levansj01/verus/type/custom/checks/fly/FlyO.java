package me.levansj01.verus.type.custom.checks.fly;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PlayerLocation;

@CheckInfo(
        type = CheckType.FLY,
        subType = "O",
        friendlyName = "Strafe",
        version = CheckVersion.RELEASE,
        minViolations = -5.0D,
        maxViolations = 100,
        logData = true
)
public class FlyO extends MovementCheck {
    private int lastBypassTick, threshold, constant;

    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        int totalTicks = this.playerData.getTotalTicks();
        if (!this.playerData.isFlying()) {
            int lastTicks = totalTicks - this.lastBypassTick, i;
            if (lastTicks >= 20) {
                double distanceXZSquared = playerLocation.distanceXZSquared(playerLocation2);
                if (distanceXZSquared > 0.025D) {
                    if (!playerLocation2.getGround() && !playerLocation.getGround()) {
                        lastTicks = 0;
                    } else {
                        lastTicks = 1;
                    }
                    int i2 = lastTicks;
                    double degrees = Math.toDegrees(-Math.atan2(playerLocation2.getX() - playerLocation.getX(), playerLocation2.getZ() - playerLocation.getZ())),
                            min = Math.min(Math.abs(MathUtil.getDistanceBetweenAngles360(degrees, playerLocation2.getYaw())), Math.abs(MathUtil.getDistanceBetweenAngles360(degrees, playerLocation.getYaw())));
                    lastTicks = 180;
                    double d = Math.min((double)lastTicks - min, min);
                    if (Math.abs(playerLocation.getYaw() - playerLocation2.getYaw()) > 1.0F) {
                        this.constant = 5;
                    }
                    if (Math.max(Math.abs(45.0D - Math.abs(MathUtil.getDistanceBetweenAngles360(degrees, playerLocation2.getYaw())) % 90.0D), Math.abs(45.0D - Math.abs(MathUtil.getDistanceBetweenAngles360(degrees, playerLocation.getYaw())) % 90.0D)) < 0.0025D && d > 0.0025D) {
                        i = this.threshold;
                        lastTicks = i;
                        this.threshold = i + 1;
                        if (lastTicks > 0) {
                            i = this.constant;
                            this.constant = i - 1;
                            if (i > 0) {
                                this.run(() -> {
                                    if (totalTicks - this.lastBypassTick >= 20) {
                                        if ((new Cuboid(playerLocation2)).expand(0.4D, 0.5D, 0.4D).checkBlocks(this.player, this.player.getWorld(), (w) -> !MaterialList.LIQUIDS.contains(w))) {
                                            this.handleViolation(String.format("ANG: %.5f SPD: %.2f G: %s", d, Math.sqrt(distanceXZSquared), i2), 0.5D);
                                        } else {
                                            this.lastBypassTick = totalTicks;
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        this.threshold = 0;
                        this.decreaseVL(0.02D);
                    }
                }
            }
        }
    }
}
