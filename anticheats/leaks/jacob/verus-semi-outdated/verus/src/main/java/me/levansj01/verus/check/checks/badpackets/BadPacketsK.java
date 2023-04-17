package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "K",
        friendlyName = "Invalid Interact",
        version = CheckVersion.RELEASE,
        maxViolations = 1
)
public class BadPacketsK extends Check implements PacketHandler {

    public void handle(VPacketPlayInUseEntity vPacket) {
        if (vPacket.getId() == this.player.getEntityId() && this.playerData.isSurvival()) {
            this.handleViolation(() -> String.format("E: %s", this.player.getEntityId()));
        }
    }

}