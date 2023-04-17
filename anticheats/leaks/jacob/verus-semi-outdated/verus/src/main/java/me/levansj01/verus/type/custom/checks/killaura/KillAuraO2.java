package me.levansj01.verus.type.custom.checks.killaura;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.type.Loader;

@CheckInfo(
        type = CheckType.KILL_AURA,
        subType = "O2",
        friendlyName = "SwitchAura",
        version = CheckVersion.RELEASE,
        minViolations = -1.0D,
        unsupportedAtleast = ClientVersion.V1_9,
        unsupportedServerAtleast = ServerVersion.v1_17_R1
)
public class KillAuraO2 extends Check implements PacketHandler {
    private int entityId, ticks;

    public KillAuraO2() {
        if (Loader.getUsername().equalsIgnoreCase("UniversoCraft")) {
            this.setMaxViolation(5);
        }
    }

    public void handle(VPacketPlayInUseEntity vPacket) {
        if (vPacket.getAction().isAttack() && vPacket.isPlayer()) {
            int packetId = vPacket.getId();
            if (this.entityId == packetId) {
                this.decreaseVL(0.05D);
            } else {
                if (this.ticks <= 1 && this.playerData.isSurvival()) {
                    this.handleViolation();
                } else {
                    this.decreaseVL(0.05D);
                }
                this.ticks = 0;
            }
            this.entityId = packetId;
        }
    }

    public void handle(VPacketPlayInFlying vPacket) {
        this.ticks += 1;
    }
}
