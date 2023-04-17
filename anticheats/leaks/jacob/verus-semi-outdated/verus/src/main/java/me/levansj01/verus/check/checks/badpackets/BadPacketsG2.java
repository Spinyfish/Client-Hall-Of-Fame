package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "G2",
        friendlyName = "Ping Spoof",
        version = CheckVersion.RELEASE
)
public class BadPacketsG2 extends Check {
}