package me.levansj01.verus.type.premium.checks.inventory;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.version.*;
import me.levansj01.verus.compat.packets.*;

import java.util.*;

@CheckInfo(
        type = CheckType.INVENTORY, 
        subType = "B", 
        friendlyName = "Inventory", 
        version = CheckVersion.RELEASE, 
        unsupportedAtleast = ClientVersion.V1_9, 
        maxViolations = 5
)
public class InventoryB extends Check implements PacketHandler {
    private int stage = 0;
    private int slot = -999;

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        this.stage = 0;
        this.decreaseVL(0.025);
    }

    @Override
    public void handle(VPacketPlayInWindowClick vPacket) {
        int slot = vPacket.getSlot();
        if (vPacket.isShiftClick() && vPacket.getButton() == 0 && !this.playerData.hasLag()) {
            if (this.stage == 0 && vPacket.isItem()) {
                this.stage = 1;
            } else if (this.stage == 1 && !vPacket.isItem() && Objects.equals(this.slot, slot)) {
                this.handleViolation("", 1.0, true);
                this.stage = 0;
            } else {
                this.stage = 0;
            }
        } else {
            this.stage = 0;
        }
        this.slot = slot;
    }
}
