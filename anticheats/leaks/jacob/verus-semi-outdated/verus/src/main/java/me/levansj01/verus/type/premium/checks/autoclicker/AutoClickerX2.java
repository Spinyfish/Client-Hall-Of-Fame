package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.version.*;
import me.levansj01.verus.storage.*;
import me.levansj01.verus.compat.packets.*;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "X2",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        minViolations = -1.0,
        unsupportedAtleast = ClientVersion.V1_9,
        logData = true
)

public class AutoClickerX2 extends Check implements PacketHandler {
    private long lastFlying;
    private int done;
    private boolean place;
    private int swings;

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        int done = this.done;
        this.done = done + 1;
        if (done >= 19) {
            int maxClicksPerSecond = StorageEngine.getInstance().getVerusConfig().getMaxClicksPerSecond();
            if (this.swings > maxClicksPerSecond) {
                this.handleViolation(String.format("S: %s", this.swings), (this.swings - maxClicksPerSecond) / 2.0);
            } else {
                this.decreaseVL(0.1);
            }
            this.swings = 0;
            this.done = 0;
        }
        if (this.place) {
            this.place = false;
        }
        this.lastFlying = this.playerData.getTimestamp();
    }

    @Override
    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.place = true;
    }

    @Override
    public void handle(VPacketPlayInArmAnimation vPacket) {
        if (!this.playerData.isSwingDigging() && !this.place) {
            if (this.playerData.getVersion().before(ClientVersion.V1_8) && this.playerData.getTimestamp() - this.lastFlying > 110) {
                return;
            }
            this.swings += 1;
        }
    }
}
