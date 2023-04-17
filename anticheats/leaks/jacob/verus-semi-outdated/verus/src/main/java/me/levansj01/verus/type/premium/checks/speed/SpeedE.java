package me.levansj01.verus.type.premium.checks.speed;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PlayerLocation;

@CheckInfo(
        type = CheckType.SPEED,
        subType = "E",
        friendlyName = "OmniSprint",
        version = CheckVersion.RELEASE,
        minViolations = -1.0D,
        maxViolations = 20
)
public class SpeedE extends MovementCheck {
    private Double lastAngle = null;
    private int threshold = 0;

    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        if (this.playerData.isSprinting(true) && playerLocation2.getGround() && playerLocation.getGround() && !this.playerData.hasLag()) {
            if (!this.playerData.isTeleporting(3) && this.playerData.getVelocityTicks() > this.playerData.getPingTicks()) {
                double x = playerLocation2.getX() - playerLocation.getX(),
                        z = playerLocation2.getZ() - playerLocation.getZ(),
                        degrees = Math.toDegrees(-Math.atan2(x, z)),
                        distance = Math.min(MathUtil.getDistanceBetweenAngles360(degrees, playerLocation2.getYaw()), MathUtil.getDistanceBetweenAngles360(degrees, playerLocation.getYaw()));
                if (this.lastAngle != null && x != 0.0D && z != 0.0D) {
                    double distance2 = MathUtil.getDistanceBetweenAngles360(this.lastAngle, distance);
                    if (distance > 50.0D) {
                        if (distance2 < 5.0D) {
                            int i = this.threshold;
                            this.threshold = i + 1;
                            if (i > 4) {
                                this.run(() -> {
                                    if ((new Cuboid(this.playerData.getLocation())).expand(0.5D, 0.5D, 0.5D).checkBlocks(this.player, this.player.getWorld(), (w) -> !MaterialList.BAD_VELOCITY.contains(w))) {
                                        this.handleViolation(String.format("D: %s LD: %s", distance, distance2));
                                    } else {
                                        this.threshold = -20;
                                    }
                                });
                                this.threshold = 0;
                            }
                        }
                    } else {
                        this.threshold = 0;
                    }
                }
                this.lastAngle = distance;
                return;
            }
        }
        this.lastAngle = null;
        this.threshold = 0;
    }
}
