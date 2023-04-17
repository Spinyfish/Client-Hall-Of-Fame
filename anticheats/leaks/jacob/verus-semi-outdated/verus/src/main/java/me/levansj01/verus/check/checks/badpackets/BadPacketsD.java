package me.levansj01.verus.check.checks.badpackets;

import java.util.function.Supplier;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockDig;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockDig.PlayerDigType;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "D",
        friendlyName = "KillAura",
        version = CheckVersion.RELEASE,
        maxViolations = 1
)
public class BadPacketsD extends Check implements PacketHandler {

    public void handle(VPacketPlayInBlockDig vPacket) {
        if (vPacket.getType() == PlayerDigType.RELEASE_USE_ITEM) {
            switch (vPacket.getFace()) {
                case UP:
                case DOWN:
                case EAST:
                case WEST: {
                    this.handleViolation(() -> String.format("F: %s", vPacket.getFace().name()), 1.0, this.violations < 2.0);
                }
            }
        }
    }

}
