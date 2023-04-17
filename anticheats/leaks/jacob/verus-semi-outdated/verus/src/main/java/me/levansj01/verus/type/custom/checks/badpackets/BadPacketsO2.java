package me.levansj01.verus.type.custom.checks.badpackets;

import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "O2",
        friendlyName = "Invalid Interact",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9,
        schem = true
)
public class BadPacketsO2 extends TransactionCheck {
    public BadPacketsO2() {
        super((packet) -> packet instanceof VPacketPlayInBlockPlace);
    }
}
