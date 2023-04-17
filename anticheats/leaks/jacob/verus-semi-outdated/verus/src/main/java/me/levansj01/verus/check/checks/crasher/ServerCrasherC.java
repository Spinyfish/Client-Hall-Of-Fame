package me.levansj01.verus.check.checks.crasher;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;

@CheckInfo(
        type = CheckType.SERVER_CRASHER,
        subType = "C",
        friendlyName = "Server Crasher",
        version = CheckVersion.RELEASE,
        maxViolations = 1
)
public class ServerCrasherC extends Check implements PacketHandler {

    public void handle(VPacketPlayInFlying vPacket) {
        if (Math.abs(vPacket.getY()) > 1.0E9) {
            this.handleViolation();
            this.playerData.fuckOff();
        }
    }

}