package me.levansj01.verus.check.checks.crasher;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInArmAnimation;
import me.levansj01.verus.compat.packets.VPacketPlayInBlockPlace;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.compat.packets.VPacketPlayInHeldItemSlot;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.storage.StorageEngine;

@CheckInfo(
        type = CheckType.SERVER_CRASHER,
        subType = "B",
        friendlyName = "Server Crasher",
        version = CheckVersion.RELEASE,
        maxViolations = 1,
        unsupportedAtleast = ClientVersion.V1_9
)
public class ServerCrasherB extends Check implements PacketHandler {

    private int places;
    private long lastSwitch;
    private int swings;
    private int switches;

    public void handle(VPacketPlayInFlying vPacket) {
        this.swings = this.places = 0;
    }

    public void handle(int i, int j) {
        if (StorageEngine.getInstance().getVerusConfig().isSchemBans() && this.playerData.isSurvival()) {
            this.handleViolation(String.format("T: %s A: %s", i, j));
            this.playerData.fuckOff();
        }
    }

    public void handle(VPacketPlayInArmAnimation vPacket) {
        this.swings += 1;
        if (this.swings > 200) {
            this.handle(1, this.swings);
        }

    }

    public void handle(VPacketPlayInBlockPlace vPacket) {
        this.places += 1;
        if (this.places > 200) {
            this.handle(2, this.places);
        }

    }

    public void handle(VPacketPlayInHeldItemSlot vPacket) {
        long time = this.playerData.getTimestamp() - this.lastSwitch;
        if (time > 100L) {
            this.switches = 0;
            this.lastSwitch = this.playerData.getTimestamp();
        }
        if ((this.switches += 1) > 400) {
            this.handle(3, this.switches);
        }
    }
}
