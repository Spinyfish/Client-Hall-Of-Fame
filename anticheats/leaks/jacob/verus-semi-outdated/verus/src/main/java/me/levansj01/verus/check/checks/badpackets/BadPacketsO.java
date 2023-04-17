package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "O",
        friendlyName = "Invalid Interact",
        version = CheckVersion.RELEASE,
        minViolations = -1.5,
        maxViolations = 5,
        schem = true,
        unsupportedAtleast = ClientVersion.V1_17
)
public class BadPacketsO extends Check implements PacketHandler {

    private boolean placed;

    public void handle(VPacketPlayInFlying vPacket) {
        if (this.placed) {
            long time = this.playerData.getTimestamp() - this.playerData.getLastLastFlying();
            if (time > 40L) {
                this.handleViolation("", 0.5);
            } else {
                this.decreaseVL(0.05);
            }
            this.placed = false;
        }
    }

    public void handle(VPacketPlayInBlockPlace vPacket) {
        if (this.playerData.isSurvival()) {
            long time = this.playerData.getTimestamp() - this.playerData.getLastFlying();
            if (time < 5L) {
                this.placed = true;
            } else {
                this.decreaseVL(0.1);
            }
        }

    }
}