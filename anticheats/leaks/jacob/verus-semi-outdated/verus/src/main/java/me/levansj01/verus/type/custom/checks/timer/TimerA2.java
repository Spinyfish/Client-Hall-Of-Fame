package me.levansj01.verus.type.custom.checks.timer;

import java.util.concurrent.TimeUnit;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.handler.TeleportCheck;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayOutPosition;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.TIMER,
        subType = "A2",
        friendlyName = "Timer",
        version = CheckVersion.RELEASE,
        minViolations = -1.0D,
        maxViolations = 50,
        unsupportedAtleast = ClientVersion.V1_9
)
public class TimerA2 extends Check implements PacketHandler, TeleportCheck {
    private long lastFlag, offset = -100;
    private Long lastPacket = null;
    private static final long CHECK_TIME_LINEAR = toNanos(45);

    public void handle(VPacketPlayInFlying vPacket) {
        long nanos = this.playerData.getNanos();
        if (this.lastPacket != null) {
            this.offset += toNanos(50) - nanos - this.lastPacket;
            if (this.offset > CHECK_TIME_LINEAR) {
                if (this.playerData.shouldHaveReceivedPing()) {
                    int totalTicks = this.playerData.getTotalTicks();
                    if (totalTicks > 300 && this.playerData.isSpawned() && !this.playerData.isVehicle()) {
                        boolean isMoved = this.playerData.isMoved();
                        if (fromNanos(nanos - this.lastFlag) <= 1) {
                            totalTicks = 1;
                        } else {
                            totalTicks = 0;
                        }
                        int totalTicks1 = totalTicks;
                        double d = isMoved ? 1.0D : 0.1D;
                        if (totalTicks1 != 0) {
                            d /= 2.0D;
                        }
                        this.handleViolation(() -> String.format("O: %sms M: %s F: %s", fromNanos(this.offset), totalTicks1, isMoved), d);
                        this.lastFlag = nanos;
                    }
                }
                this.offset = 0;
            } else {
                this.decreaseVL(0.0025D);
            }
        }
        this.lastPacket = nanos;
    }

    public void handle(VPacketPlayOutPosition vPacket, long l) {
        this.offset -= toNanos(50);
    }

    public static long fromNanos(long nanoSeconds) {
        return TimeUnit.NANOSECONDS.toMillis(nanoSeconds);
    }

    public static long toNanos(long milliSeconds) {
        return TimeUnit.MILLISECONDS.toNanos(milliSeconds);
    }
}
