package me.levansj01.verus.type.custom.checks.badpackets;

import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.compat.packets.VPacketPlayInArmAnimation;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "S2",
        friendlyName = "Invalid Interact",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9,
        unsupportedServerAtleast = ServerVersion.v1_11_R1,
        schem = true
)
public class BadPacketsS2 extends TransactionCheck {
    public BadPacketsS2() {
        super((packet) -> packet instanceof VPacketPlayInArmAnimation);
    }
}
