package me.levansj01.verus.type.premium.checks.inventory;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.version.*;
import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.packets.*;

@CheckInfo(
        type = CheckType.INVENTORY,
        subType = "A",
        friendlyName = "Inventory",
        version = CheckVersion.RELEASE,
        minViolations = -5.0,
        maxViolations = 5,
        unsupportedAtleast = ClientVersion.V1_9,
        unsupportedServerAtleast = ServerVersion.v1_11_R1,
        schem = true
)
public class InventoryA extends Check implements PacketHandler {
    private boolean clicked = false;

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        if (this.clicked) {
            if (this.playerData.getTimestamp() - this.playerData.getLastLastFlying() > 40) {
                this.handleViolation();
            } else {
                this.decreaseVL(0.05);
            }
            this.clicked = false;
        }
    }

    @Override
    public void handle(VPacketPlayInWindowClick vPacket) {
        if (this.playerData.getTimestamp() - this.playerData.getLastFlying() < 5) {
            this.clicked = vPacket.isShiftClick();
        } else {
            this.decreaseVL(0.1);
        }
    }
}
