package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.compat.packets.*;
import me.levansj01.verus.util.java.*;
import me.levansj01.verus.compat.api.wrapper.*;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "E",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        minViolations = -1.0,
        logData = true
)

public class AutoClickerE extends Check implements PacketHandler {
    private int ticks;
    private final BasicDeque tickList;

    public AutoClickerE() {
        this.ticks = 0;
        this.tickList = new CappedQueue(41);
    }

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        if (this.playerData.isDigging() && !this.playerData.isAbortedDigging()) {
            this.ticks += 1;
        }
    }

    @Override
    public void handle(VPacketPlayInBlockDig vPacket) {
        if (vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
            this.ticks = 0;
        } else if (vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
            BlockPosition blockPosition = vPacket.getBlockPosition();
            Direction face = vPacket.getFace();
            if (blockPosition.equals(this.playerData.getDiggingBlock()) && !face.equals(this.playerData.getDiggingBlockFace())) {
                this.tickList.addFirst(this.ticks);
                if (this.tickList.size() > 40) {
                    double deviation = MathUtil.deviation(this.tickList);
                    if (deviation < 0.325) {
                        this.handleViolation(String.format("D: %s", deviation), 1.325 - deviation, true);
                    } else {
                        this.decreaseVL(0.25);
                    }
                    this.tickList.clear();
                }
            }
            this.ticks = 0;
        }
    }
}
