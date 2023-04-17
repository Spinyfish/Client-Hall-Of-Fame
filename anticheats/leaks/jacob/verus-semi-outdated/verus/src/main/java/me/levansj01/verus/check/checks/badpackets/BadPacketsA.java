package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "A",
        friendlyName = "Timer",
        version = CheckVersion.RELEASE,
        maxViolations = 5
)
public class BadPacketsA extends Check implements PacketHandler {
    private int packets;

    public void handle(VPacketPlayInFlying vPacket) {
        if (!this.playerData.isVehicle() && !vPacket.isPos()) {
            this.packets += 1;
            if (this.packets > 20) {
                double buff;
                if (packets > 22) {
                    buff = 2.0;
                } else {
                    buff = 0.2;
                }

                this.handleViolation(() -> String.format("P: %s | %s", packets, this.playerData.getMaxPingTicks()), buff);
            } else {
                this.decreaseVL(1.0E-5);
            }
        } else {
            this.packets = 0;
        }

    }
}