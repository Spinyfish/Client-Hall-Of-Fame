package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInEntityAction;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "E",
        friendlyName = "Invalid Move",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9,
        schem = true
)
public class BadPacketsE extends Check implements PacketHandler {
    private boolean sentSprint;
    private boolean sentSneak;

    public void handle(VPacketPlayInFlying vPacket) {
        this.sentSprint = this.sentSneak = false;
    }


    public void handle(VPacketPlayInEntityAction vPacket) {
        if (!this.playerData.isVehicle() && this.playerData.isSurvival() && !this.playerData.isTeleportingV2() && !this.playerData.hasLag()) {
            VPacketPlayInEntityAction.PlayerAction action = vPacket.getAction();
            if (action.isSprint()) {
                if (this.sentSprint) {
                    this.handleViolation("Sprint");
                }
                this.sentSprint = true;
            } else if (action.isSneak()) {
                if (this.sentSneak) {
                    this.handleViolation("Sneak");
                }
                this.sentSneak = true;
            }
        }
    }

}
