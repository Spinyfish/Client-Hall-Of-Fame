package me.levansj01.verus.type.custom.checks.speed;

import me.levansj01.verus.check.MovementCheck2;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.java.DoubleWrapper;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PacketLocation;

@CheckInfo(
        type = CheckType.SPEED,
        subType = "F",
        friendlyName = "NoSlowdown",
        version = CheckVersion.DEVELOPMENT,
        unsupportedAtleast = ClientVersion.V1_9,
        unsupportedVersions = {ClientVersion.V1_7},
        minViolations = -2.5D
)
public class SpeedF extends MovementCheck2 {
    private int threshold = 0;
    private Double lastAngleDiff = null;

    public void handle(PacketLocation packetLocation, PacketLocation packetLocation2, long l) {
        if (!this.playerData.isTeleporting(5) && !this.playerData.isTeleportingV2()) {
            int velocityTicks = this.playerData.getVelocityTicks(), pingTicks = this.playerData.getMaxPingTicks();
            if (velocityTicks > pingTicks * 2 && !this.playerData.isVehicle() && this.playerData.isBlocking()) {
                double movementSpeed = this.playerData.getMovementSpeed(), lastMovementSpeed = movementSpeed / 0.2D;
                if (lastMovementSpeed > 4.0D) {
                    return;
                }
                double distanceBetweenAngles360 = MathUtil.getDistanceBetweenAngles360(((float)Math.toDegrees(-Math.atan2(packetLocation2.getX() - packetLocation.getX(), packetLocation2.getZ() - packetLocation.getZ()))), packetLocation2.getYaw()),
                        xz = MathUtil.hypot(packetLocation2.getX() - packetLocation.getX(), packetLocation2.getZ() - packetLocation.getZ());
                DoubleWrapper doubleWrapper = new DoubleWrapper(0.0437D);
                doubleWrapper.addAndGet(Math.max((double)(Integer)this.playerData.getSpeedLevel().get() * 0.012D, 0.0D));
                if (movementSpeed > 0.2D) {
                    doubleWrapper.addAndGet(doubleWrapper.get() * lastMovementSpeed);
                }
                if (this.lastAngleDiff != null && packetLocation2.isGround()) {
                    if (this.lastAngleDiff > 0.5D) {
                        doubleWrapper.addAndGet(0.03D);
                    }
                    if (xz > doubleWrapper.get()) {
                        velocityTicks = this.threshold;
                        pingTicks = this.playerData.getMaxPingTicks();
                        pingTicks += 2;
                        if (velocityTicks > pingTicks * 2) {
                            this.handleViolation(() -> String.format("D: %.4f A: %s", xz - doubleWrapper.get(), this.lastAngleDiff));
                            this.threshold = 0;
                        }
                    } else {
                        this.threshold = 0;
                        this.decreaseVL(0.05D);
                    }
                }
                this.lastAngleDiff = distanceBetweenAngles360;
                return;
            }
        }
        this.threshold = 0;
        this.decreaseVL(0.001D);
        this.lastAngleDiff = null;
    }
}
