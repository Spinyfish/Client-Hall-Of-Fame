package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInSteerVehicle;

@CheckInfo(
        friendlyName = "InvalidSteer",
        type = CheckType.BAD_PACKETS,
        subType = "B1",
        version = CheckVersion.RELEASE,
        maxViolations = 5,
        minViolations = -2.0
)
public class BadPacketsB1 extends Check implements PacketHandler {

    public void handle(VPacketPlayInSteerVehicle vPacket) {
        if (Math.abs(vPacket.getForward()) > 0.9800000190734863 || Math.abs(vPacket.getStrafe()) > 0.9800000190734863) {
            this.handleViolation();
        }
    }

}