package me.levansj01.verus.check.checks.timer;

import java.util.concurrent.TimeUnit;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.TIMER,
        subType = "A",
        friendlyName = "Timer",
        version = CheckVersion.RELEASE,
        minViolations = -1.0D,
        maxViolations = 60,
        logData = true,
        unsupportedAtleast = ClientVersion.V1_9
)
public class TimerA extends Check implements PacketHandler {
    private long lastFlag;
    private long lastBow;
    private static final long CHECK_TIME_LINEAR = toNanos(45);
    private long offset = -100;
    private boolean place;
    private Long lastPacket = null;

    public void handle(VPacketPlayInFlying var1) {
        long playerDataNanos = this.playerData.getNanos();
        if (this.place) {
            this.lastBow = playerDataNanos;
            this.place = false;
        }
        if (this.lastPacket != null) {
            long lastPlayerDataNanos = toNanos(50) - playerDataNanos - this.lastPacket;
            this.offset += lastPlayerDataNanos;
            if (this.offset > CHECK_TIME_LINEAR) {
                long playerDataMilli = fromNanos(playerDataNanos - this.lastBow);
                int lastPlayerDataMilli = playerDataMilli < 50 ? 1 : 0;
                int lastPlayerDataMilli$ = lastPlayerDataMilli;
                playerDataMilli = fromNanos(playerDataNanos - this.lastFlag);
                if ((playerDataMilli > (1) || lastPlayerDataMilli$ != 0) && this.playerData.shouldHaveReceivedPing()) {
                    lastPlayerDataMilli = this.playerData.getTotalTicks();
                    if (lastPlayerDataMilli > 400) {
                        if (!this.playerData.isTeleporting(3) && !this.playerData.isTeleportingV2() && this.playerData.isSpawned() && !this.playerData.isVehicle()) {
                            long lastTeleport = this.playerData.getLastTeleport();
                            if (!this.playerData.isMoved() && lastPlayerDataMilli$ == 0) {
                                lastPlayerDataMilli = 0;
                            } else {
                                lastPlayerDataMilli = 1;
                            }
                            int lastPlayerDataMilli$$ = lastPlayerDataMilli;
                            long time = this.playerData.getTimestamp();
                            this.handleViolation(() -> String.format("O: %sms F: %sms B: %sms T: %sms M: %s", fromNanos(this.offset),
                                    fromNanos(playerDataNanos - this.lastFlag),
                                    fromNanos(playerDataNanos - this.lastBow),
                                    lastTeleport == 0 ? -1L : time - lastTeleport,
                                    lastPlayerDataMilli$$
                            ), lastPlayerDataMilli$$ != 0 ? 1.0D : 0.3D);
                        }
                    }
                }
                this.lastFlag = playerDataNanos;
                this.offset = 0;
            } else {
                this.decreaseVL(0.0025D);
            }
        }
        this.lastPacket = playerDataNanos;
    }

    public static long toNanos(long milliSeconds) {
        return TimeUnit.MILLISECONDS.toNanos(milliSeconds);
    }

    public static long fromNanos(long nanoSeconds) {
        return TimeUnit.NANOSECONDS.toMillis(nanoSeconds);
    }

    public void handle(VPacketPlayInBlockPlace vPacket) {
        if (vPacket.isItem()) {
            this.place = true;
        }
    }
}
