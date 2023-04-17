package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.compat.packets.*;
import me.levansj01.verus.compat.api.wrapper.*;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "J",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        minViolations = -1.0
)

public class AutoClickerJ extends Check implements PacketHandler {
    private int lastTicks;
    private int placeTicks;
    private double value;
    private int ticks;
    private int threshold;
    private int abortTicks;

    @Override
    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.placeTicks = 0;
    }

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        if (this.playerData.isDigging() && !this.playerData.isAbortedDigging()) {
            this.ticks += 1;
        }
        this.abortTicks += 1;
    }

    public AutoClickerJ() {
        this.ticks = 0;
        this.abortTicks = 0;
        this.lastTicks = -1;
        this.threshold = 0;
        this.placeTicks = 0;
        this.value = 0.0;
    }

    @Override
    public void handle(VPacketPlayInBlockDig vPacket) {
        if (this.placeTicks > 1) {
            if (vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
                this.ticks = 0;
            } else if (vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
                BlockPosition blockPosition = vPacket.getBlockPosition();
                Direction face = vPacket.getFace();
                if (blockPosition.equals(this.playerData.getDiggingBlock()) && !face.equals(this.playerData.getDiggingBlockFace()) && !this.playerData.isPlacing()) {
                    this.decreaseVL(0.001);
                    if (this.ticks == this.lastTicks && this.abortTicks < 4) {
                        this.value += 0.3 + this.threshold * 0.2;
                        if (this.value > 20.0) {
                            this.handleViolation();
                            this.value = 0.0;
                        }
                        this.threshold += 1;
                    } else {
                        this.value -= Math.min(this.value, 1.0);
                        this.threshold = 0;
                    }
                    this.lastTicks = this.ticks;
                }
                this.abortTicks = 0;
                this.ticks = 0;
            }
        }
    }
}
