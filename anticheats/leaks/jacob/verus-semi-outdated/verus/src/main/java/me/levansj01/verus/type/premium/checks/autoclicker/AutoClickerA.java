package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.compat.packets.*;
import me.levansj01.verus.util.java.*;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "A",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        minViolations = -5.0,
        maxViolations = 10,
        logData = true
)

public class AutoClickerA extends Check implements PacketHandler {
    private int ticks;
    private boolean swung;
    private final BasicDeque intervals;
    private boolean place;

    @Override
    public void handle(VPacketPlayInArmAnimation vPacket) {
        this.swung = true;
    }

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        if (this.swung && !this.playerData.isSwingDigging() && !this.place && this.playerData.isSurvival() && this.playerData.getLastAttackTicks() < (1200)) {
            if (this.ticks < 8) {
                this.intervals.addFirst(this.ticks);
                if (this.intervals.size() >= 40) {
                    double deviation = MathUtil.deviation(this.intervals);
                    double n = (0.325 - deviation) * 2.0 + 0.675;
                    if (n > 0.0) {
                        this.handleViolation(String.format("D: %s", deviation), n);

                    } else {
                        this.decreaseVL(-n);
                    }
                    this.intervals.clear();
                }
            }
            this.ticks = (0);
        }
        this.place = false;
        this.swung = false;
        this.ticks += 1;
    }

    @Override
    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.place = true;
    }

    public AutoClickerA() {
        this.intervals = new CappedQueue(40);
        this.ticks = 0;
    }
}
