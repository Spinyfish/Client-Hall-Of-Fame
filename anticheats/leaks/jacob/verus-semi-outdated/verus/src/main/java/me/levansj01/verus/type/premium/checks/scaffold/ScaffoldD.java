package me.levansj01.verus.type.premium.checks.scaffold;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.compat.packets.*;

import java.util.*;

@CheckInfo(
        type = CheckType.SCAFFOLD,
        subType = "D",
        friendlyName = "Scaffold",
        version = CheckVersion.RELEASE,
        minViolations = -1.0,
        maxViolations = 1,
        schem = true
)
public class ScaffoldD extends Check implements PacketHandler {
    private Integer lastSlot = -999;

    @Override
    public void handle(VPacketPlayInHeldItemSlot vPacket) {
        if (Objects.equals(vPacket.getSlot(), this.lastSlot) && this.playerData.getTotalTicks() > 200) {
            this.handleViolation();
        } else {
            this.decreaseVL(0.025);
        }
        this.lastSlot = vPacket.getSlot();
    }
}
