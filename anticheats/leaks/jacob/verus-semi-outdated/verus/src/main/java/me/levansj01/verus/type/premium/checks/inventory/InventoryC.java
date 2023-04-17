package me.levansj01.verus.type.premium.checks.inventory;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.compat.packets.*;

import java.util.*;

import me.levansj01.verus.data.*;

@CheckInfo(
        type = CheckType.INVENTORY,
        subType = "C",
        friendlyName = "Inventory",
        version = CheckVersion.RELEASE,
        maxViolations = 5,
        schem = true
)
public class InventoryC extends Check implements PacketHandler {
    private int ticks = 0;
    
    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        if (this.playerData.isSprinting(true) && this.playerData.isInventoryOpen()) {
            this.ticks += 1;
        } else {
            this.ticks = 0;
            this.decreaseVL(0.001);
        }
    }

    @Override
    public void handle(VPacketPlayInWindowClick vPacket) {
        if (vPacket.getSlot() != -999 && !this.playerData.isTeleportingV2() && this.playerData.isSpawned() && this.playerData.isMoved() && this.ticks > 3) {
            this.handleViolation(String.format("T: %s", this.ticks));
            PlayerData playerData = this.playerData;
            Objects.requireNonNull(playerData);
            this.run(playerData::closeInventory);
        }
    }
}
