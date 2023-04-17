package me.levansj01.verus.check.checks.fly;

import java.util.Objects;
import java.util.Set;
import me.levansj01.verus.check.MovementCheck2;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.type.VerusTypeLoader;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.location.PacketLocation;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;

@CheckInfo(
        type = CheckType.FLY,
        subType = "L",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        maxViolations = 100,
        minViolations = -1.0D
)
public class FlyL extends MovementCheck2 {

    public void handle(PacketLocation packetLocation, PacketLocation packetLocation1, long var3) {
        if (packetLocation.isGround() && packetLocation.getY() % 0.5D * 16.0D % 1.0D != 0.0D
                && packetLocation.getY() % 0.5D * 64.0D != 1.0D && packetLocation.getY() % 0.5D != 0.0D
                && packetLocation1.getY() % 1.0D != 0.0D && packetLocation.getY() % 1.0D != 0.09375D
                && Math.abs(packetLocation.getY() % 0.5D - 0.015555072702202466D) > 1.0E-12D && !packetLocation1.isTeleport()) {
            if (!VerusTypeLoader.isEnterprise()) {
                if (this.playerData.isTeleporting(2)) {
                    return;
                }
            }
            if (!this.playerData.canFly() && this.playerData.getVelocityTicks() > this.playerData.getPingTicks() && !this.playerData.isVehicle()) {
                int totalTicks = this.playerData.getTotalTicks();
                if (totalTicks > 200L) {
                    World world = this.player.getWorld();
                    Cuboid cuboid = Cuboid.withLimit(packetLocation, packetLocation1, 16).add(-0.5D, 0.5D, -0.42D, 1.8D, -0.5D, 0.5D);
                    Cuboid cuboid1 = Cuboid.withLimit(packetLocation, packetLocation1, 16).add(-0.5D, 0.5D, -2.0D, 0.0D, -0.5D, 0.5D);
                    this.run(() -> {
                        Entity entity = null;
                        for (Entity entity1 : this.player.getNearbyEntities(2.5D, 2.5D, 2.5D)) {
                            if (entity1 instanceof Boat || entity1 instanceof Minecart) {
                                continue;
                            }
                            entity = entity1;
                        }

                        if (cuboid1.checkBlocks(this.player, world, material -> !MaterialList.BOUND_UP.contains(material))) {
                            Player player = this.player;
                            Set<Material> materials = MaterialList.FULLY_PASSABLE;
                            Objects.requireNonNull(materials);
                            if (cuboid.checkBlocks(player, world, materials::contains)) {
                                this.handleViolation(String.format("F: %s T: %s M: %s",
                                        packetLocation.getY() % 1.0D,
                                        packetLocation1.getY() % 1.0D,
                                        this.playerData.getNonMoveTicks())
                                );
                            }
                        }

                    });
                    return;
                }
            }
        }

        this.decreaseVL(0.001D);
    }
}