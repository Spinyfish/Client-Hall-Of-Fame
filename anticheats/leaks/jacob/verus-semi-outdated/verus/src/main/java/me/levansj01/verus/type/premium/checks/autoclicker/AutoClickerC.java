package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.packets.*;
import me.levansj01.verus.verus2.data.player.*;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "C",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        minViolations = -5.0,
        maxViolations = 30,
        logData = true,
        butterfly = true
)

public class AutoClickerC extends PacketCheck {
    private int normal;
    private int flags;
    private int stage;

    @Override
    public void handle(VPacket vPacket, long n) {
        if (this.stage == 0) {
            if (vPacket instanceof VPacketPlayInArmAnimation) {
                this.stage = 1;
            }
        } else if (this.stage == 1) {
            if (vPacket instanceof VPacketPlayInBlockDig) {
                this.stage = 2;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 2) {
            if (vPacket instanceof VPacketPlayInBlockDig && ((VPacketPlayInBlockDig) vPacket).getType() == VPacketPlayInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
                this.flags += 1;
                this.run();
                this.stage = 0;
            } else if (vPacket instanceof VPacketPlayInArmAnimation) {
                this.stage = 3;
            } else {
                int stage = 0;
                this.normal = stage;
                this.flags = stage;
                this.stage = stage;
            }
        } else if (this.stage == 3) {
            if (vPacket instanceof VPacketPlayInFlying) {
                this.stage = 4;
            } else {
                int stage2 = 0;
                this.normal = stage2;
                this.flags = stage2;
                this.stage = stage2;
            }
        } else if (this.stage == 4) {
            if (vPacket instanceof VPacketPlayInBlockDig && ((VPacketPlayInBlockDig) vPacket).getType() == VPacketPlayInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
                this.normal += 1;
                this.run();
                this.stage = 0;
            } else {
                int stage3 = 0;
                this.normal = stage3;
                this.flags = stage3;
                this.stage = stage3;
            }
        }
    }

    public void run() {
        int n = this.normal - this.flags;
        if ((n != (1) && this.flags > 0) || this.flags > (5)) {
            if (n > (2) || n < (-2)) {
                TickerMap tickerMap = this.playerData.getTickerMap();
                if (tickerMap.get(TickerType.DOUBLE_SWING) - tickerMap.get(TickerType.LEGIT_SWING) > (20)) {
                    this.handleViolation(String.format("D: %s", n), 0.75, false);
                }
            } else {
                this.decreaseVL(0.5);
            }
        }
    }

    public AutoClickerC() {
        this.stage = 0;
        this.flags = 0;
        this.normal = 0;
    }
}
