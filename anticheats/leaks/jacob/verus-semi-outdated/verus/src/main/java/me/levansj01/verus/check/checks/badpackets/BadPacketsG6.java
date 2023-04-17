package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.storage.StorageEngine;

@CheckInfo(
        friendlyName = "Ping Spoof",
        type = CheckType.BAD_PACKETS,
        subType = "G6",
        version = CheckVersion.RELEASE,
        maxViolations = 10,
        minViolations = -2.0,
        unsupportedServerAtleast = ServerVersion.v1_17_R1
)
public class BadPacketsG6 extends Check implements PacketHandler {

    private static final int TIME = StorageEngine.getInstance().getVerusConfig().getPingTimeout() * 20;

    public void handle(VPacketPlayInUseEntity vPacket) {
        if (this.playerData.getTotalTicks() > TIME && this.playerData.isSentTransaction() && !this.playerData.isReceivedTransaction() && vPacket.isPlayer()) {
            this.handleViolation();
        } else {
            this.decreaseVL(0.05);
        }
    }

}