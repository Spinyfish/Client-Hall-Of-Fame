package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInKeepAlive;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "G5",
        friendlyName = "Ping Spoof",
        version = CheckVersion.RELEASE,
        minViolations = -1.0,
        maxViolations = 50
)
public class BadPacketsG5 extends Check implements PacketHandler {
    private long lastKeepAlive;

    public void handle(VPacketPlayInKeepAlive vPacket) {
        if (this.playerData.shouldHaveReceivedPing() && this.lastKeepAlive > vPacket.getId()) {
            int totalTicks = this.playerData.getTotalTicks();
            if (totalTicks > 160L) {
                long keepAliveDst = this.lastKeepAlive - vPacket.getId();
                this.handleViolation(() -> String.format("D: %s", keepAliveDst));
            }
        }

        this.lastKeepAlive = vPacket.getId();
    }

}