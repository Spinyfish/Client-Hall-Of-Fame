package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "G1",
        friendlyName = "Ping Spoof",
        version = CheckVersion.RELEASE,
        minViolations = -2.0,
        maxViolations = 10
)
public class BadPacketsG1 extends Check {
    // Yeah this check is scam
}