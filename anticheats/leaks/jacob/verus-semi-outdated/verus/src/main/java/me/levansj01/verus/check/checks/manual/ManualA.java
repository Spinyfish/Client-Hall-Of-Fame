package me.levansj01.verus.check.checks.manual;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;

@CheckInfo(
        type = CheckType.MANUAL,
        subType = "A",
        friendlyName = "Manual",
        logData = true
)
public class ManualA extends Check {
}
