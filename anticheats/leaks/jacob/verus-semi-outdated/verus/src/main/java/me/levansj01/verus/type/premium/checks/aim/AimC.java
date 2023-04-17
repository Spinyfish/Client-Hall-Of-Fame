package me.levansj01.verus.type.premium.checks.aim;

import me.levansj01.verus.check.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.util.location.*;

@CheckInfo(
        type = CheckType.AIM_ASSIST,
        subType = "C",
        friendlyName = "Aim",
        version = CheckVersion.RELEASE,
        minViolations = -2.5
)
public class AimC extends AimCheck {
    @Override
    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        if (this.playerData.getLastAttackTicks() > (100)) {
            return;
        }
        float abs = Math.abs(playerLocation2.getYaw() - playerLocation.getYaw()),
                abs2 = Math.abs(playerLocation2.getPitch() - playerLocation.getPitch());
        if (abs > 0.0f && abs < 0.01 && abs2 > 0.2) {
            this.handleViolation(String.format("Y: %s P: %s", abs, abs2));

        } else {
            this.violations -= Math.min(this.violations + 2.5, 0.05);
        }
    }
}
