package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.version.*;
import me.levansj01.verus.verus2.data.player.*;
import me.levansj01.verus.compat.packets.*;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "B",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9,
        minViolations = -5.0,
        maxViolations = 20,
        logData = true,
        butterfly = true
)

public class AutoClickerB extends Check implements PacketHandler {
    private int ticks;
    private boolean first;
    private boolean swing;
    private int stage;

    @Override
    public void handle(VPacketPlayInArmAnimation vPacket) {
        if (this.stage == 1) {
            this.stage = 2;
        } else if (this.stage == 3 || this.stage == 0) {
            this.swing = true;
        }
    }

    @Override
    public void handle(VPacketPlayInBlockDig vPacket) {
        if (vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.START_DESTROY_BLOCK) {
            if (this.stage == 0 && this.ticks > 1) {
                this.stage = 1;
                this.first = this.swing;
            } else {
                this.stage = 0;
            }
        } else if (vPacket.getType() == VPacketPlayInBlockDig.PlayerDigType.ABORT_DESTROY_BLOCK) {
            if (this.stage == 3) {
                if (this.swing) {
                    TickerMap tickerMap = this.playerData.getTickerMap();
                    int n = tickerMap.get(TickerType.DOUBLE_SWING) - tickerMap.get(TickerType.LEGIT_SWING);
                    if (this.ticks > 1 && n > 20) {
                        this.handleViolation(String.valueOf(n), 1.0, true);
                    }
                } else if (this.first) {
                    this.decreaseVL(0.3);
                }
            }
            this.stage = 0;
        }
    }

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        if (this.stage == 2) {
            this.stage = 3;
        }
        this.swing = false;
        this.ticks += 1;
    }

    @Override
    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.ticks = 0;
    }
}
