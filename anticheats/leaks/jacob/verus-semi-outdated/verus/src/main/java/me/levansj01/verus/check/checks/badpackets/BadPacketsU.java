package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInHeldItemSlot;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "U",
        friendlyName = "Invalid Inventory",
        version = CheckVersion.RELEASE,
        minViolations = -1.5D,
        maxViolations = 5,
        schem = true
)
public class BadPacketsU extends Check implements PacketHandler {
    private Long lastFlying;
    private Long swapped;

    public void handle(VPacketPlayInFlying vPacket) {
        if (this.lastFlying != null && this.swapped != null) {
            long time = this.playerData.getTimestamp();
            long last;
            if (this.playerData.getVersion().after(ClientVersion.V1_8)) {
                last = time - this.lastFlying;
                if (last > 60) {
                    return;
                }
            }
            last = time - this.playerData.getLastLastFlying();
            if (last > 45) {
                last = time - this.swapped;
                if (last > 40) {
                    this.handleViolation(String.format("S: %s F: %s", time - this.swapped, time - this.lastFlying), 0.5D);
                }
            }
            this.decreaseVL(0.05D);
        }
        this.lastFlying = this.playerData.getLastFlying();
        this.swapped = null;
    }

    public void handle(VPacketPlayInHeldItemSlot vPacket) {
        if (this.playerData.isSurvival()) {
            long time = this.playerData.getTimestamp();
            long lastFlying = time - this.playerData.getLastFlying();
            if (lastFlying < 5) {
                this.swapped = time;
            } else {
                this.decreaseVL(0.1D);
            }
        }
    }
}
