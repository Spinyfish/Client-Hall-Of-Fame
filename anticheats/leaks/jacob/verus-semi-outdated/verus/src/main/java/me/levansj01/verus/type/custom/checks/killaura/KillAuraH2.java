package me.levansj01.verus.type.custom.checks.killaura;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInArmAnimation;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity.EntityUseAction;
import me.levansj01.verus.data.version.ClientVersion;

@CheckInfo(
        type = CheckType.KILL_AURA,
        subType = "H2",
        friendlyName = "KillAura",
        version = CheckVersion.RELEASE,
        minViolations = -4.0D
)
public class KillAuraH2 extends Check implements PacketHandler {
    private Long lastFlying, attack, arm;

    public void handle(VPacketPlayInUseEntity vPacket) {
        if (vPacket.getAction() == EntityUseAction.ATTACK) {
            this.attack = this.playerData.getTimestamp();
        }
    }

    public void handle(VPacketPlayInFlying vPacket) {
        if (this.lastFlying != null && this.attack != null && this.arm != null) {
            long abs = Math.abs(this.attack - this.arm);
            if (abs <= 2) {
                if (!this.playerData.getVersion().before(ClientVersion.V1_9)) {
                    abs = this.playerData.getTimestamp() - this.lastFlying;
                    if (abs > 60) {
                        return;
                    }
                }
                abs = this.playerData.getTimestamp() - this.attack;
                if (abs > 40 && this.playerData.isSurvival()) {
                    this.handleViolation(String.format("F: %s DIFF: %s", this.playerData.getTimestamp() - this.arm, this.attack - this.arm));
                } else {
                    this.decreaseVL(1.2D);
                }
            }
        }
        this.lastFlying = this.playerData.getLastFlying();
        this.arm = this.attack = null;
    }

    public void handle(VPacketPlayInArmAnimation vPacket) {
        this.arm = this.playerData.getTimestamp();
    }
}
