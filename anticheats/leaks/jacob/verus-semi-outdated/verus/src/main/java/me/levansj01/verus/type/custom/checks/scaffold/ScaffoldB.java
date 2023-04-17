package me.levansj01.verus.type.custom.checks.scaffold;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.api.wrapper.BlockPosition;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;

@CheckInfo(
        type = CheckType.SCAFFOLD,
        subType = "B",
        friendlyName = "Scaffold",
        version = CheckVersion.RELEASE,
        schem = true,
        minViolations = -1.5D,
        maxViolations = 10
)
public class ScaffoldB extends Check implements PacketHandler {
    private double lastZ, lastY, lastX;
    private BlockPosition lastBlockPosition;
    private Float lastYaw;

    public ScaffoldB() {
        this.lastZ = this.lastX = this.lastY = 0.0D;
    }

    public void handle(VPacketPlayInBlockPlace vPacket) {
        if (vPacket.getFace() != 255) {
            BlockPosition blockPosition = vPacket.getPosition();
            double x = vPacket.getBlockX(), y = vPacket.getBlockY(), z = vPacket.getBlockZ();
            if (this.lastBlockPosition != null && blockPosition.nearby(this.lastBlockPosition) && x == this.lastX && y == this.lastY && z == this.lastZ) {
                float yaw = this.playerData.getLocation().getYaw();
                if (this.lastYaw != null) {
                    if (Math.abs(yaw - this.lastYaw) > 20.0D) {
                        this.handleViolation();
                    } else {
                        this.decreaseVL(0.025D);
                    }
                }
                this.lastYaw = yaw;
            }
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.lastBlockPosition = blockPosition;
        }
    }

    public void handle(VPacketPlayInFlying vPacket) {
        this.decreaseVL(0.025D);
    }
}
