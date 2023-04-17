package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "H",
        friendlyName = "Invalid Direction",
        version = CheckVersion.RELEASE,
        minViolations = -1.0,
        maxViolations = 15,
        unsupportedAtleast = ClientVersion.V1_9
)
public class BadPacketsH extends Check implements PacketHandler {

    private Float oldPitch = null;
    private Float lastPitch = null;
    private Float oldYaw = null;
    private Float lastYaw = null;

    public void handle(VPacketPlayInFlying vPacket) {
        if (vPacket.isLook()) {
            float yaw = vPacket.getYaw();
            float pitch = vPacket.getPitch();
            if (!this.playerData.isTeleportingV2()) {
                if (!this.playerData.isTeleporting(5)) {
                    if (!this.playerData.hasRecentTeleport(10, this.playerData.getLastLocation(),
                            this.playerData.getLocation()) && !this.playerData.isVehicle()
                            && !this.playerData.hasFast(this.playerData.getTimestamp() - this.playerData.getTransactionPing())
                            && this.playerData.isSpawned() && !this.playerData.isSuffocating()) {
                        if (this.oldYaw == null || this.oldYaw != yaw || this.oldPitch == null || this.oldPitch != pitch) {
                            this.oldPitch = this.oldYaw = null;
                            if (this.lastYaw != null && this.lastPitch != null && this.lastYaw == yaw && this.lastPitch == pitch) {
                                this.handleViolation(() -> String.format("%f %f", lastYaw, lastPitch));
                            } else {
                                this.decreaseVL(0.05);
                            }
                        }
                        this.lastYaw = yaw;
                        this.lastPitch = pitch;
                        return;
                    }
                }
            }
            this.oldYaw = yaw;
            this.oldPitch = pitch;
        }
    }

}