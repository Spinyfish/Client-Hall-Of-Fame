package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "R",
        friendlyName = "KillAura",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9
)
public class BadPacketsR extends Check implements PacketHandler {
    private boolean sent;
    private boolean interact;

    public void handle(VPacketPlayInFlying vPacket) {
        this.sent = this.interact = false;
    }

    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.sent = true;
    }

    public void handle(VPacketPlayInUseEntity vPacket) {
        if (vPacket.getAction().isInteract()) {
            if (this.sent && !this.interact) {
                this.handleViolation("", 1.0, true);
                this.sent = false;
            }
            this.interact = true;
        }
    }
}