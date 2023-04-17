package me.levansj01.verus.type.custom.checks.misc;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockDig;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.type.Loader;

@CheckInfo(
        type = CheckType.MISC,
        subType = "F",
        friendlyName = "Nuker",
        minViolations = -1.0D,
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9
)
public class MiscF extends Check implements PacketHandler {
    private boolean sent;

    public MiscF() {
        if (Loader.getUsername().equals("UniversoCraft")) {
            this.setMaxViolation(50);
        }
    }

    public void handle(VPacketPlayInFlying vPacket) {
        if (!this.playerData.isTeleportingV2()) {
            if (!this.playerData.isTeleporting(2)) {
                return;
            }
        }
        this.sent = false;
    }

    public void handle(VPacketPlayInBlockDig vPacket) {
        switch (vPacket.getType().ordinal()) {
            case 1:
                this.sent = false;
                break;
            case 2:
                if (!this.playerData.isTeleportingV2()) {
                    if (!this.playerData.isTeleporting(2) && this.playerData.isSurvival()) {
                        if (this.sent) {
                            this.handleViolation();
                        } else {
                            this.decreaseVL(0.2D);
                            this.sent = true;
                        }
                    }
                }
        }
    }
}
