package me.levansj01.verus.type.custom.checks.badpackets;

import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockDig;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "J2",
        friendlyName = "Invalid Interact",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9
)
public class BadPacketsJ2 extends TransactionCheck {
    public BadPacketsJ2() {
        super((packet) -> packet instanceof VPacketPlayInBlockDig);
    }
}
