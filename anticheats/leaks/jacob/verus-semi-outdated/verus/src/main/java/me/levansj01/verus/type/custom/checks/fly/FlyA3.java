package me.levansj01.verus.type.custom.checks.fly;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import me.levansj01.verus.check.MovementCheck2;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.handler.TeleportCheck;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.packets.VPacketPlayOutPosition;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.location.PacketLocation;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

@CheckInfo(
        type = CheckType.FLY,
        subType = "A3",
        friendlyName = "Fly",
        minViolations = -20.0D,
        version = CheckVersion.DEVELOPMENT,
        heavy = true
)
public class FlyA3 extends MovementCheck2 implements TeleportCheck {
    private int singles, deltas, lastBypassTick, z, y, x;
    private PacketLocation valid;

    public void check(PacketLocation packetLocation, PacketLocation packetLocation2, BooleanSupplier booleanSupplier, BooleanSupplier booleanSupplier2, Runnable runnable) {
        int totalTicks = this.playerData.getTotalTicks();
        if (this.lastBypassTick <= totalTicks) {
            Player player = this.playerData.getPlayer();
            Cuboid cuboid = packetLocation2.to(packetLocation, 25).move(0.0D, -0.5D, 0.0D).expand(0.4D, 0.5D, 0.4D);
            this.run(() -> {
                Material material = MaterialList.AIR;
                Objects.requireNonNull(material);
                if (!cuboid.checkBlocks(player, player.getWorld(), material::equals)) {
                    this.valid = packetLocation2;
                    runnable.run();
                    this.decreaseVL(1.0D);
                } else {
                    Iterator<Entity> entities = player.getNearbyEntities(1.0D + cuboid.getWidthX(), 1.0D + cuboid.getWidthY(), 1.0D + cuboid.getWidthZ()).iterator();
                    while (true) {
                        if (!entities.hasNext()) {
                            if (booleanSupplier.getAsBoolean()) {
                                this.handleViolation();
                            }
                            if (this.valid != null && StorageEngine.getInstance().getVerusConfig().isHeavyPullback() && booleanSupplier2.getAsBoolean()) {
                                NMSManager.getInstance().sendTeleport(player, this.valid.look(this.playerData.getLocation()).withY(Math.min(this.valid.getY(), this.playerData.getLocation().getY())));
                            }
                            break;
                        }
                        Entity entity = entities.next();
                        if (entity instanceof Boat || entity instanceof Minecart) {
                            this.lastBypassTick = totalTicks + 100;
                            return;
                        }
                    }
                }
            });
        }
    }

    public void handle(PacketLocation packetLocation, PacketLocation packetLocation2, long l) {
        if (packetLocation2.isGround()) {
            int x = (int) packetLocation2.getX(), y = (int) packetLocation2.getY(), z = (int) packetLocation2.getZ();
            if (this.x == x && this.y == y && this.z == z) {
                if (this.violations > this.getMinViolation()) {
                    this.check(packetLocation, packetLocation2, () -> {
                        int singles = this.singles;
                        this.singles = singles + 1;
                        int pingTicks = this.playerData.getMaxPingTicks2();
                        pingTicks += 2;
                        return singles > pingTicks * 2;
                    }, () -> false, () -> {
                        this.deltas = 0;
                        this.singles = 0;
                    });
                }
            } else {
                this.check(packetLocation, packetLocation2, () -> {
                    int deltas = this.deltas;
                    this.deltas = deltas + 1;
                    return deltas > this.playerData.getPingTicks() / 2;
                }, () -> {
                    if (!(this.violations > 2.0D)) {
                        return this.deltas + this.singles > this.playerData.getPingTicks() / 2;
                    }
                    return true;
                }, () -> {
                    this.deltas = 0;
                    this.singles = 0;
                });
                this.x = x;
                this.y = y;
                this.z = z;
            }
        }
    }

    public void handle(VPacketPlayOutPosition vPacket, long l) {
        if (this.valid != null && !this.valid.sameXZ(vPacket)) {
            this.valid = null;
        }
    }
}
