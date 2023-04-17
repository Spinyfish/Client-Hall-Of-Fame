package me.levansj01.verus.check.checks.timer;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.java.BasicDeque;
import me.levansj01.verus.util.java.CappedQueue;
import me.levansj01.verus.util.java.MathUtil;

@CheckInfo(
        type = CheckType.TIMER,
        subType = "B",
        friendlyName = "Timer",
        version = CheckVersion.RELEASE,
        logData = true,
        maxViolations = 40,
        minViolations = -3.0D
)
public class TimerB extends Check implements PacketHandler {
    private Long lastPacket;
    private Long lastLook;
    private final BasicDeque delays = new CappedQueue(40);

    public void handle(VPacketPlayInBlockPlace var1) {
        if ((var1.isUse() || var1.isEmpty()) && this.lastLook != null) {
            if (this.playerData.getTimestamp() - this.lastLook < (5) && this.playerData.getVersion().afterEq(ClientVersion.V1_17)) {
                this.delays.addFirst(250);
            }
        }
    }

    public void handle(VPacketPlayInFlying vPacket) {
        long time = this.playerData.getTimestamp();
        if (this.playerData.shouldHaveReceivedPing()) {
            int totalTicks = this.playerData.getTotalTicks();
            if (totalTicks > 200) {
                if (!this.playerData.isTeleporting(3) && !this.playerData.isTeleportingV2() && this.playerData.isSpawned() && !this.playerData.isVehicle() && !this.playerData.hasLag() && vPacket.isPos() && (this.playerData.getVersion().after(ClientVersion.V1_8) && this.playerData.getVersion() != ClientVersion.VERSION_UNSUPPORTED || this.playerData.isMoved())) {
                    if (this.lastPacket != null) {
                        this.delays.addFirst(time - this.lastPacket);
                        totalTicks = this.delays.size();
                        if (totalTicks == 40) {
                            double average = MathUtil.average(this.delays);
                            double lastAverage = 50.0D / average;
                            if (average <= 48.0D) {
                                this.handleViolation(String.format("T: %.1f M: %.2f", average, lastAverage), Math.min(Math.floor(lastAverage / 0.6D), 2.0D));
                            } else {
                                this.decreaseVL(0.25D);
                            }
                            this.delays.clear();
                        }
                    }
                    this.lastPacket = time;
                }
            }
        }
        if (vPacket.isLook()) {
            this.lastLook = time;
        }
    }
}
