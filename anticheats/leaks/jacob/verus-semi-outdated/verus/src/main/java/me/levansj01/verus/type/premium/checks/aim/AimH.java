package me.levansj01.verus.type.premium.checks.aim;

import me.levansj01.verus.check.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.util.location.*;

@CheckInfo(
        type = CheckType.AIM_ASSIST,
        subType = "H",
        friendlyName = "Aim",
        version = CheckVersion.EXPERIMENTAL,
        minViolations = -1.0
)
public class AimH extends AimCheck {
    @Override
    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        float abs = Math.abs(playerLocation.getYaw() - playerLocation2.getYaw()),
                abs2 = Math.abs(playerLocation.getPitch() - playerLocation2.getPitch());
        if (this.playerData.getLastAttackTicks() < (3) && abs > 0.0f && abs < 0.8 && abs2 > 0.279 && abs2 < 0.28090858) {
            this.handleViolation(String.format("Y: %s P: %s", abs, abs2));

        } else {
            this.decreaseVL(1.0E-4);
        }
    }
}
