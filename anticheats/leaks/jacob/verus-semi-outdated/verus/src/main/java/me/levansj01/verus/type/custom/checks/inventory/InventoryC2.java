package me.levansj01.verus.type.custom.checks.inventory;

import me.levansj01.verus.check.MovementCheck2;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.util.java.DoubleWrapper;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PacketLocation;
import me.levansj01.verus.verus2.data.player.TickerType;

@CheckInfo(
        type = CheckType.INVENTORY,
        subType = "C2",
        version = CheckVersion.RELEASE,
        friendlyName = "Inventory",
        schem = true,
        logData = true
)
public class InventoryC2 extends MovementCheck2 {
    public void handle(PacketLocation packetLocation, PacketLocation packetLocation2, long l) {
        if (!this.playerData.isTeleporting(5) && !this.playerData.isTeleportingV2()) {
            int tickerMap = this.playerData.getTickerMap().get(TickerType.WINDOW_CLICK);
            if (tickerMap <= 1) {
                tickerMap = this.playerData.getVelocityTicks();
                int maxPingTicks = this.playerData.getMaxPingTicks();
                if (tickerMap > maxPingTicks * 2 && !this.playerData.isFallFlying() && !this.playerData.isGliding()) {
                    double xz = MathUtil.hypot(packetLocation2.getX() - packetLocation.getX(), packetLocation2.getZ() - packetLocation.getZ());
                    boolean isGround = !packetLocation.isGround() || !packetLocation2.isGround();
                    DoubleWrapper doubleWrapper = new DoubleWrapper(0.215D);
                    maxPingTicks = (Integer)this.playerData.getSpeedLevel().get();
                    double d = packetLocation2.isGround() ? 0.0573D : 0.0175D;
                    doubleWrapper.addAndGet(Math.max(maxPingTicks, 0) * d);
                    if (xz > doubleWrapper.get()) {
                        boolean hasLagOrFast = this.playerData.hasLag() || this.playerData.hasFast();
                        if (!hasLagOrFast && !isGround) {
                            d = 1.0D;
                        } else {
                            d = 0.2D;
                        }
                        this.handleViolation(String.format("%.3f %s %s %s", xz - doubleWrapper.get(), this.playerData.isSprinting(true), hasLagOrFast, isGround), d);
                    }
                }
            }
        }
    }
}
