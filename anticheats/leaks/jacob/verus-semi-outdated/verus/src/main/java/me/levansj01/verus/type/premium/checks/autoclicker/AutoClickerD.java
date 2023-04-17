package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.util.java.*;
import me.levansj01.verus.compat.packets.*;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "D",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        minViolations = -1.0,
        maxViolations = 10,
        logData = true
)

public class AutoClickerD extends Check implements PacketHandler {
    private final BasicDeque averageTicks;
    private boolean digging;
    private int ticks;
    private boolean swung;
    private int done;
    private int lastTicks;
    private boolean abortedDigging;

    public AutoClickerD() {
        this.averageTicks = new CappedQueue(50);
        this.ticks = 0;
        this.done = 0;
        this.lastTicks = 0;
    }

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        if (this.swung && !this.digging && this.playerData.getLastAttackTicks() < 1200) {
            if (this.ticks < 7) {
                this.averageTicks.addFirst(this.ticks);
            }
            if (this.averageTicks.size() > 40) {
                double average = MathUtil.average(this.averageTicks);
                if (average < 2.5) {
                    if (this.ticks > 3 && this.ticks < 20 && this.lastTicks < 20) {
                        this.violations -= Math.min(this.violations, 0.25);
                        this.done = 0;

                    } else {
                        int done = this.done;
                        this.done = done + 1;
                        if (done > 600.0 / (average * 1.5)) {
                            this.handleViolation(String.format("A: %s", average), 1.0);
                            this.done = 0;
                        }
                    }
                } else {
                    this.done = 0;
                }
            }
            this.lastTicks = this.ticks;
            this.ticks = 0;
        }
        if (this.abortedDigging) {
            this.digging = false;
            this.abortedDigging = false;
        }
        this.swung = false;
        this.ticks += 1;
    }

    @Override
    public void handle(VPacketPlayInBlockDig vPacket) {
        if (vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
            this.digging = true;
            this.abortedDigging = false;
        } else if (vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
            this.abortedDigging = true;
        }
    }

    @Override
    public void handle(VPacketPlayInArmAnimation vPacket) {
        this.swung = true;
    }
}
