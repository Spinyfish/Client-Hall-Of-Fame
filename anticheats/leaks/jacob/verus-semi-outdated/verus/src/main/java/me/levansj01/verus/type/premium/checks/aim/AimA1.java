package me.levansj01.verus.type.premium.checks.aim;

import me.levansj01.verus.check.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.util.location.*;

import java.util.*;

@CheckInfo(
        type = CheckType.AIM_ASSIST,
        subType = "A1",
        friendlyName = "Aimbot",
        version = CheckVersion.RELEASE,
        maxViolations = 10
)
public class AimA1 extends AimCheck {
    private float lastYawChange;

    @Override
    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        if (this.playerData.getLastAttackTicks() < (3)) {
            float abs = Math.abs(playerLocation.getYaw() - playerLocation2.getYaw());
            int round;
            if (abs > 1.0f && (round = Math.round(abs)) == abs) {
                if (Objects.equals(abs, this.lastYawChange)) {
                    this.handleViolation(String.format("Y: %s", abs));
                }
                this.lastYawChange = (float) round;

            } else {
                this.lastYawChange = 0.0f;
            }
        }
    }
}
