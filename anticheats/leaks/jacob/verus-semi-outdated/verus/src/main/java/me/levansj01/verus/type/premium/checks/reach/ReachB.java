package me.levansj01.verus.type.premium.checks.reach;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.data.version.*;
import me.levansj01.verus.compat.packets.*;

@CheckInfo(
        type = CheckType.REACH,
        subType = "B",
        friendlyName = "Reach",
        version = CheckVersion.RELEASE,
        unsupportedAtleast = ClientVersion.V1_9,
        maxViolations = 4
)
public class ReachB extends Check implements PacketHandler {
    @Override
    public void handle(VPacketPlayInUseEntity vPacketPlayInUseEntity) {
        if (vPacketPlayInUseEntity.getAction() == VPacketPlayInUseEntity.EntityUseAction.INTERACT_AT) {
            double bodyX = vPacketPlayInUseEntity.getBodyX(),
                    bodyY = vPacketPlayInUseEntity.getBodyY(),
                    bodyZ = vPacketPlayInUseEntity.getBodyZ();
            if ((Math.abs(bodyX) > 0.4004004004004004 || Math.abs(bodyZ) > 0.4004004004004004 || bodyY > 1.901901901901902 || bodyY < -0.1001001001001001) && vPacketPlayInUseEntity.isPlayer() && (this.playerData.getTracker() == null || this.playerData.getTracker().get(vPacketPlayInUseEntity.getId()) != null)) {
                this.handleViolation(String.format("X: %s Y: %s Z: %s", bodyX, bodyY, bodyZ), 1.0, true);
            }
        }
    }
}
