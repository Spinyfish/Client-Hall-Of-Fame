package me.levansj01.verus.type.premium.checks.aim;

import me.levansj01.verus.check.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.util.location.*;

@CheckInfo(
        type = CheckType.AIM_ASSIST,
        subType = "A",
        friendlyName = "Aimbot",
        version = CheckVersion.RELEASE,
        maxViolations = 2,
        logData = true
)
public class AimA extends AimCheck {
    @Override
    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        float abs = Math.abs(playerLocation.getYaw() - playerLocation2.getYaw());
        if (abs >= 1.0f && abs % 0.1f == 0.0f) {
            if (abs % 1.0f == 0.0f) {
                ++this.violations;
            }
            if (abs % 10.0f == 0.0f) {
                ++this.violations;
            }
            if (abs % 30.0f == 0.0f) {
                ++this.violations;
            }
            this.handleViolation(String.format("Y: %s", abs));
        }
        float abs2 = Math.abs(playerLocation.getPitch() - playerLocation2.getPitch());
        if (abs2 >= 1.0f && abs2 % 0.1f == 0.0f) {
            if (abs2 % 1.0f == 0.0f) {
                ++this.violations;
            }
            if (abs2 % 10.0f == 0.0f) {
                ++this.violations;
            }
            if (abs2 % 30.0f == 0.0f) {
                ++this.violations;
            }
            this.handleViolation(String.format("P: %s", abs2));
        }
    }
}
