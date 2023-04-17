package me.levansj01.verus.check.checks.crasher;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInCustomPayload;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;

@CheckInfo(
        type = CheckType.SERVER_CRASHER,
        subType = "A",
        friendlyName = "Server Crasher",
        version = CheckVersion.RELEASE,
        maxViolations = 5,
        logData = true
)
public class ServerCrasherA extends Check implements PacketHandler {

    private int threshold = 0;

    public void handle(VPacketPlayInFlying vPacket) {
        this.threshold -= Math.min(this.threshold, 1);
    }

    public void handle(VPacketPlayInCustomPayload vPacket) {
        String channel = vPacket.getChannel();
        if (channel.equals("MC|BOpen") || channel.equals("MC|BEdit")) {
            this.threshold += 2;
            if (this.threshold > 4) {
                this.handleViolation(String.format("C: %s", channel), this.threshold / 4.0);
                if (this.violations > this.getMaxViolation()) {
                    this.playerData.fuckOff();
                }
            }
        }
    }

}