package me.levansj01.verus.check.checks.badpackets;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.data.transaction.teleport.Teleport;

@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "G4",
        friendlyName = "Ping Spoof",
        version = CheckVersion.RELEASE,
        minViolations = -1.0
)
public class BadPacketsG4 extends Check {

    private int threshold;

    public void accept(long time, Teleport teleport) {
        long teleportTime = time - teleport.getTime();
        int transactionPing = this.playerData.getTransactionPing();
        long l = teleportTime * 10L;
        if (l + 100L < transactionPing) {
            int totalTicks = this.playerData.getTotalTicks();
            if (totalTicks > 100L) {
                int threshold1 = this.threshold;
                totalTicks = threshold1;
                this.threshold = threshold1 + 1;
                if (totalTicks > 3L) {
                    this.handleViolation(() -> String.format("Tr: %s K: %s Te: %s", transactionPing, this.playerData.getPing(), teleportTime), (double)this.threshold / 4.0);
                    this.playerData.setTransactionPing((int)teleportTime);
                }
                return;
            }
        }
        this.threshold = 0;
    }

}
