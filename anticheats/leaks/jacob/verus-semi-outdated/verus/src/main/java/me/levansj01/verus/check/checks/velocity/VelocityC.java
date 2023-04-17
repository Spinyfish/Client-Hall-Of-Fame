package me.levansj01.verus.check.checks.velocity;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.util.location.PlayerLocation;

@CheckInfo(type=CheckType.VELOCITY, subType="C", friendlyName="NoVelocity", version=CheckVersion.RELEASE, minViolations=-2.0)
public class VelocityC
        extends Check
        implements PacketHandler {
    public void handle(VPacketPlayInFlying<?> vPacketPlayInFlying) {
        PlayerLocation playerLocation = this.playerData.getLastLastLocation();
        PlayerLocation playerLocation2 = this.playerData.getLocation();
        if (this.playerData.getVerticalVelocityTicks() > this.playerData.getMoveTicks() - 1 && this.playerData.getLastVelY() > 0.0 && this.playerData.getVerticalVelocityTicks() > this.playerData.getMaxPingTicks()) {
            double d = playerLocation2.getY() - playerLocation.getY();
            if (d > 0.0) {
                this.decreaseVL(1.0);
            }
            this.handleViolation(String.format((String)"T: %s | %s", (Object[])new Object[]{this.playerData.getVerticalVelocityTicks(), this.playerData.getMaxPingTicks()}));
            this.playerData.setResetVelocity(true);
        }
    }
}