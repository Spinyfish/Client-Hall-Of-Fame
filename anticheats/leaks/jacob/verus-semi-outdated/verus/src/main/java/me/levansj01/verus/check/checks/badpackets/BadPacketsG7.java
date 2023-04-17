package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.verus2.data.player.TickerMap;
import me.levansj01.verus.verus2.data.player.TickerType;

@CheckInfo(
        friendlyName = "Ping Spoof",
        type = CheckType.BAD_PACKETS,
        subType = "G7",
        version = CheckVersion.RELEASE,
        maxViolations = 5,
        minViolations = -2.0,
        unsupportedAtleast = ClientVersion.V1_9,
        unsupportedServerAtleast = ServerVersion.v1_17_R1
)
public class BadPacketsG7 extends Check implements PacketHandler {

    private static final int TIME = StorageEngine.getInstance().getVerusConfig().getPingTimeout() * 20;

    public void handle(VPacketPlayInUseEntity vPacket) {
        if (StorageEngine.getInstance().getVerusConfig().isPingKickCheck()) {
            TickerMap tickerMap = this.playerData.getTickerMap();
            int totalTicks = tickerMap.get(TickerType.TOTAL);
            int lastRTransactionTick = totalTicks - tickerMap.get(TickerType.LAST_RECEIVED_TRANSACTION);
            int lastSTransactionTick = totalTicks - tickerMap.get(TickerType.LAST_SENT_TRANSACTION);
            if (totalTicks > TIME && lastRTransactionTick >= TIME) {
                if (lastSTransactionTick < 10L && vPacket.isPlayer()) {
                    this.handleViolation(() -> String.format("S: %s D: %s", lastSTransactionTick, lastRTransactionTick));
                    return;
                }
            }
            this.decreaseVL(0.01);
        }

    }

}
