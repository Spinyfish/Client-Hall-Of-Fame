package me.levansj01.verus.type.custom.checks.fly;

import java.util.Iterator;

import me.levansj01.verus.check.MovementCheck2;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.util.location.PacketLocation;
import me.levansj01.verus.verus2.data.player.TickerType;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

@CheckInfo(
        type = CheckType.FLY,
        subType = "K2",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -1.0D,
        maxViolations = 10,
        logData = true
)
public class FlyK2 extends MovementCheck2 {
    private double lastYDiff;
    private int lastBypassTick = -10;

    public void handle(PacketLocation packetLocation, PacketLocation packetLocation2, long l) {
        int totalTicks = this.playerData.getTotalTicks();
        if (totalTicks - 100 > this.lastBypassTick) {
            double y = packetLocation.getY() - packetLocation2.getY();
            if (packetLocation.isGround() && packetLocation.getY() % 0.5D * 16.0D % 1.0D != 0.0D && packetLocation.getY() % 0.5D * 64.0D != 1.0D && packetLocation.getY() % 0.5D != 0.0D && packetLocation2.getY() % 1.0D != 0.41999998688697815D && packetLocation2.getY() % 1.0D != 0.0D && Math.abs(packetLocation.getY() % 0.5D - 0.015555072702202466D) > 1.0E-12D && packetLocation.getY() % 1.0D != 0.09375D) {
                totalTicks = this.playerData.getTickerMap().get(TickerType.TELEPORT);
                if (totalTicks > 2 && this.playerData.getVelocityTicks() > this.playerData.getPingTicks() && !this.playerData.isVehicle() && this.playerData.isSurvival() && !this.playerData.canFly()) {
                    if (!this.playerData.hasPlacedBlock(true)) {
                        if (!packetLocation2.isGround() && y < 0.078D && this.lastYDiff > 0.08D) {
                            double lastYDiff = this.lastYDiff;
                            this.run(() -> {
                                if (this.playerData.getTotalTicks() - 100 > this.lastBypassTick) {
                                    Iterator<Entity> entities = this.player.getNearbyEntities(2.5D, 2.5D, 2.5D).iterator();
                                    if (!entities.hasNext()) {
                                        this.handleViolation(String.format("L: %.3f N: %.3f", lastYDiff, y));
                                        return;
                                    }
                                    Entity entity = entities.next();
                                    if (entity instanceof Boat || entity instanceof Minecart) {
                                        this.lastBypassTick = this.playerData.getTotalTicks();
                                    }
                                }
                            });
                        }
                    }
                }
            }
            this.decreaseVL(0.001D);
            this.lastYDiff = y;
        }
    }
}
