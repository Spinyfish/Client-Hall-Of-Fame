package me.levansj01.verus.check.checks.fly;

import java.util.Iterator;
import java.util.List;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInFlying;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.location.PacketLocation;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

@CheckInfo(
        type = CheckType.FLY,
        subType = "A",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -2.5,
        maxViolations = 30,
        logData = true,
        heavy = true
)
public class FlyA extends Check implements PacketHandler {

    private int threshold;
    private boolean ground;
    private int lastBypassTick;
    private Cuboid cuboid = null;
    private int delay;

    public void handle(VPacketPlayInFlying var1) {
        PacketLocation packetLocation = this.playerData.getCurrentLocation2();
        int totalTicks = this.playerData.getTotalTicks();
        if (this.ground && packetLocation.isGround() && this.playerData.isSurvival() && totalTicks > this.lastBypassTick) {
            World world = this.player.getWorld();
            boolean pos = var1.isPos();
            if (this.cuboid != null) {
                this.delay += 1;
                if (this.delay <= StorageEngine.getInstance().getVerusConfig().getHeavyTicks() || !pos || this.cuboid.containsBlock(world, packetLocation)) {
                    return;
                }
            }

            PlayerLocation lastLocation = this.playerData.getLastLocation().clone();
            PlayerLocation location2 = this.playerData.getLocation().clone();
            this.cuboid = Cuboid.withLimit(lastLocation, location2, 16).move(0.0, -0.5, 0.0).expand(0.4, 0.5, 0.4);
            this.run(() -> {
                if (this.cuboid == null) {
                    this.cuboid = Cuboid.withLimit(lastLocation, location2, 16).move(0.0, -0.5, 0.0).expand(0.4, 0.5, 0.4);
                }

                if (!this.playerData.isTeleporting(5) && this.cuboid.checkBlocks(this.player, world, FlyA::isFullyPassable)) {

                    List<Entity> entities =  this.player.getNearbyEntities(1.0 + this.cuboid.getWidthX(),
                            1.0 + this.cuboid.getWidthY(), 1.0 + this.cuboid.getWidthZ());
                    int i = 0;

                    for (Entity entity : entities) {
                        i++;
                        if (i >= entities.size()) {
                            this.cuboid = null;
                            this.threshold += 1;
                            if (this.threshold > this.playerData.getMaxPingTicks2()) {
                                double buff = (!pos && this.playerData.hasLag()) ? 0.1 : 1.0;
                                this.handleViolation(
                                        String.format("%s > %s | l=%s p=%s", this.threshold, this.playerData.getMaxPingTicks(), this.playerData.hasLag(), pos),
                                        buff
                                );
                            }
                        }
                        if (entity instanceof Boat || entity instanceof Minecart) {
                            this.lastBypassTick = totalTicks + 100;
                        }
                    }
                } else {
                    this.threshold = 0;
                    this.decreaseVL(2.5);
                }

            });
            this.delay = 0;
        } else {
            this.threshold = 0;
            this.delay = StorageEngine.getInstance().getVerusConfig().getHeavyTicks() + 1;
        }

        this.ground = packetLocation.isGround();
    }

    public static boolean isFullyPassable(Material material) {
        switch (material.ordinal()) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                return false;
        }
    }

}