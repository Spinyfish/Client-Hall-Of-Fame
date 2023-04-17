package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInKeepAlive;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.util.java.MathUtil;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "G3",
        friendlyName = "Ping Spoof",
        minViolations = -3.0,
        maxViolations = 30,
        version = CheckVersion.RELEASE,
        logData = true
)
public class BadPacketsG3 extends Check implements PacketHandler {

    private boolean receivedKeepAlive;

    public void handle(VPacketPlayInFlying vPacket) {
        if (this.receivedKeepAlive) {
            double highest = MathUtil.highest(this.playerData.getLastTransactionPing(), this.playerData.getTransactionPing(), this.playerData.getAverageTransactionPing());
            double lowest = MathUtil.lowest(this.playerData.getLastPing(), this.playerData.getPing(), this.playerData.getAveragePing());
            if (lowest - highest > 50.0 + highest / 4.0) {
                double buff = StorageEngine.getInstance().getVerusConfig().isMoreTransactions() ? 0.25D : 1.0D;

                this.handleViolation(() -> String.format("K(%s) T(%s) %s", lowest, highest, vPacket.isPos()), buff);
            } else {
                double vl = StorageEngine.getInstance().getVerusConfig().isMoreTransactions() ? 0.3D : 0.1D;

                this.decreaseVL(vl);
            }
            this.receivedKeepAlive = false;
        }
    }

    public void handle(VPacketPlayInKeepAlive vPacket) {
        this.receivedKeepAlive = true;
    }

}