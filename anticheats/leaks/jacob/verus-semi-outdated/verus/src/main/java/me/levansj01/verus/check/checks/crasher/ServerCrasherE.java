package me.levansj01.verus.check.checks.crasher;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInSetCreativeSlot;
import org.bukkit.GameMode;

@CheckInfo(
        type = CheckType.SERVER_CRASHER,
        subType = "E",
        friendlyName = "Server Crasher",
        version = CheckVersion.DEVELOPMENT,
        maxViolations = 1,
        minViolations = -2.0,
        schem = true
)
public class ServerCrasherE extends Check implements PacketHandler {

    public void handle(VPacketPlayInSetCreativeSlot vPacket) {
        switch (this.player.getGameMode().ordinal()) {
            case 1:
            case 2:
                this.handleViolation();
            default:
        }
    }

}