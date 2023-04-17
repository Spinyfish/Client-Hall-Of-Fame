package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInArmAnimation;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockDig;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "Z",
        friendlyName = "Nuker",
        version = CheckVersion.RELEASE,
        unsupportedVersions = {ClientVersion.V1_7},
        minViolations = -1.0D,
        maxViolations = 50
)
public class BadPacketsZ extends Check implements PacketHandler {
    private int invalid;

    public void handle(VPacketPlayInBlockDig vPacket) {
        int invalid;
        switch(vPacket.getType().ordinal()) {
            case 1:
            case 2:
                if (this.playerData.shouldHaveReceivedPing() && !this.playerData.hasLag() && !this.playerData.hasFast()) {
                    invalid = this.invalid;
                    int invalid$ = invalid;
                    this.invalid = invalid + 1;
                    if (invalid$ > 2) {
                        this.handleViolation();
                    }
                }
                break;
            default:
                this.invalid = 0;
                this.decreaseVL(1.0D);
        }
    }

    public void handle(VPacketPlayInArmAnimation vPacket) {
        this.invalid = 0;
    }
}
