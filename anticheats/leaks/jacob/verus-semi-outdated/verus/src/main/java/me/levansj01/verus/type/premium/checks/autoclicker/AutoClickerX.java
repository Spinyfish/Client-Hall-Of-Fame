package me.levansj01.verus.type.premium.checks.autoclicker;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.version.*;
import me.levansj01.verus.compat.packets.*;
import me.levansj01.verus.storage.*;

@CheckInfo(
        type = CheckType.AUTO_CLICKER,
        subType = "X",
        friendlyName = "Auto Clicker",
        version = CheckVersion.RELEASE,
        minViolations = -1.0,
        maxViolations = 15,
        unsupportedAtleast = ClientVersion.V1_9,
        logData = true
)

public class AutoClickerX extends Check implements PacketHandler {
    private boolean place;
    private int done;
    private int swings;

    @Override
    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.place = true;
    }

    @Override
    public void handle(VPacketPlayInArmAnimation vPacket) {
        if (!this.playerData.hasLag() && !this.playerData.hasFast() && !this.playerData.isSwingDigging() && !this.place) {
            this.swings += 1;
        }
    }

    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        int done = this.done;
        this.done = done + 1;
        if (done >= 19) {
            if (this.playerData.getVersion().before(ClientVersion.V1_8) && (this.playerData.hasLag() || this.playerData.hasFast())) {
                this.swings = 0;
                this.done = 0;
            }
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
    }
}
