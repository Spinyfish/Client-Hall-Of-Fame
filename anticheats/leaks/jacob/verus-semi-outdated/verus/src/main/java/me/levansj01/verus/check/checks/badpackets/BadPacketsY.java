package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.api.Transactionable;
import me.levansj01.verus.compat.packets.VPacketPlayInKeepAlive;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "Y",
        friendlyName = "Blink",
        version = CheckVersion.RELEASE,
        logData = true,
        minViolations = -2.5D,
        maxViolations = 15
)
public class BadPacketsY extends Check implements PacketHandler {
    private Long lastTickDelay;
    private Long lastKeepAlive;

    public void handleIn(Transactionable transactionable) {
        if (this.playerData.shouldHaveReceivedPing() && !this.player.isDead() && !this.playerData.isVehicle() && this.playerData.isSurvival()) {
            int totalTicks = this.playerData.getTotalTicks();
            if (totalTicks > 800) {
                long ping = Math.max(50, Math.max(this.playerData.getTransactionPing(), this.playerData.getPing()));
                long time = this.playerData.getTimestamp() - this.playerData.getLastFlying();
                if (this.lastTickDelay != null) {
                    long lastTick = time - this.lastTickDelay;
                    long lastTime = this.playerData.getTimestamp() - this.lastKeepAlive;
                    if (this.playerData.getVersion().after(ClientVersion.V1_8)) {
                        totalTicks = 2000;
                    } else {
                        totalTicks = 500;
                    }
                    long total = totalTicks + ping * 2;
                    if (time > total) {
                        if (lastTick > Math.max(250, ping)) {
                            if (lastTick < 500) {
                                if (lastTime < 1000 + ping * 2) {
                                    this.handleViolation(() -> String.format("T: %s D: %s L: %s O: %s K: %s", time, lastTick, total, Math.abs(time - total), lastTime), 0.5D);
                                }
                            }
                        }
                    }
                    this.decreaseVL(0.05D);
                }
                this.lastTickDelay = time;
            }
        }
    }

    public void handle(VPacketPlayInKeepAlive vPacket) {
        this.lastKeepAlive = this.playerData.getTimestamp();
    }
}
