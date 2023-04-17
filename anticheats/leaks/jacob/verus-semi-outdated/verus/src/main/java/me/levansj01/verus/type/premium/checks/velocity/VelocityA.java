package me.levansj01.verus.type.premium.checks.velocity;

import me.levansj01.verus.check.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.check.annotation.*;
import me.levansj01.verus.check.type.*;
import me.levansj01.verus.check.version.*;
import me.levansj01.verus.compat.packets.*;
import me.levansj01.verus.util.java.*;
import me.levansj01.verus.verus2.data.player.*;
import me.levansj01.verus.util.location.*;

@CheckInfo(
        type = CheckType.VELOCITY,
        subType = "A",
        friendlyName = "vVelocity",
        version = CheckVersion.RELEASE,
        minViolations = -0.5,
        maxViolations = 5
)
public class VelocityA extends Check implements PacketHandler {
    @Override
    public void handle(VPacketPlayInFlying vPacket) {
        PlayerLocation lastLastLocation = this.playerData.getLastLastLocation(),
                location = this.playerData.getLocation();
        if (this.playerData.getVerticalVelocityTicks() > this.playerData.getMoveTicks() - 1 && this.playerData.getLastVelY() > 0.0) {
            double n = location.getY() - lastLastLocation.getY();
            if (n > 0.0) {
                double n2 = Math.ceil(n * 8000.0) / 8000.0;
                if (n2 < 0.41999998688697815 && lastLastLocation.getGround() && !location.getGround() && MathUtil.onGround(lastLastLocation.getY()) && !MathUtil.onGround(location.getY()) && this.playerData.getTickerMap().get(TickerType.EXPLOSION) > this.playerData.getMaxPingTicks() + 2) {
                    double n3 = n2 / this.playerData.getLastVelY();
                    if (n3 < 0.995) {
                        this.handleViolation(String.format("P: %.1f.", n3 * 100));
                    }
                }
                this.playerData.setResetVelocity(true);
            } else {
                this.decreaseVL(0.01);
            }
        }
    }
}
