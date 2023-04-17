package me.levansj01.verus.check.checks.fly;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;

@CheckInfo(
        type = CheckType.FLY,
        subType = "E",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -3.5,
        maxViolations = 30,
        logData = true
)
public class FlyE extends MovementCheck {

    private double threshold;
    private int lastBypassTick = -10;

    @Override
    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation1, long l) {
        if (this.playerData.getTotalTicks() - 20 < this.lastBypassTick)
            return;
        if (playerLocation.getGround() && playerLocation1.getY() > playerLocation.getY() && !this.playerData.isTeleporting() && this.playerData.isSpawned() && !this.playerData.isFallFlying() && !this.playerData.isGliding() && !this.playerData.hasPlacedBucket() && !this.playerData.isLevitating() && !this.playerData.isTeleportingV2() && !this.playerData.hasPlacedBlock(true) && !this.playerData.canFly()) {
            double deltaY = playerLocation1.getY() - playerLocation.getY();
            if (!this.playerData.getVersion().before(ClientVersion.V1_9)) {
            } else {

            }
            double d2 = 0.41999998688697815D;

            /*if (this.playerData.getVelocityTicks() <= (this.playerData.getMaxPingTicks() + 1) * 4) {
            } else {
            }*/

            boolean bool = false;
            if (this.playerData.getVelocityQueue().stream().anyMatch(velocity -> Math.abs(velocity.getOriginalY() - deltaY) <= 1.25E-4D))
                return;
            if (deltaY < d2 && (playerLocation1.getY() - d2) % 1.0D > 1.0E-15D) {
                World world = this.player.getWorld();
                Cuboid cuboid1 = Cuboid.withLimit(playerLocation, playerLocation1, 16).move(0.0D, 2.0D, 0.0D).expand(0.5D, 0.5D, 0.5D);
                Cuboid cuboid2 = Cuboid.withLimit(playerLocation, playerLocation1, 16).move(0.0D, -0.25D, 0.0D).expand(0.5D, 0.75D, 0.5D);
                int i = this.playerData.getTotalTicks();
                if (this.playerData.hasJumpBoost())
                    this.lastBypassTick = i;
                run(() -> {
                    if (this.playerData.getTotalTicks() - 20 < this.lastBypassTick)
                        return;
                    if (cuboid1.checkBlocks(this.player, world, material -> material == Material.AIR)
                            && cuboid2.checkBlocks(this.player, world, material -> !MaterialList.INVALID_SHAPE.contains(material)
                            && !MaterialList.BAD_VELOCITY.contains(material) && !MaterialList.BED.contains(material))) {
                        for (Entity entity : this.player.getNearbyEntities(2.5D, 2.5D, 2.5D)) {
                            if (entity instanceof org.bukkit.entity.Boat || entity instanceof org.bukkit.entity.Minecart) {
                                this.threshold = 0.0D;
                                decreaseVL(0.025D);
                                this.lastBypassTick = i - 100;
                                return;
                            }
                        }
                        this.threshold++;
                        handleViolation(String.format("D: %s Y: %s", deltaY, playerLocation1.getY()), this.threshold);
                    } else {
                        this.threshold = 0.0D;
                        decreaseVL(0.025D);
                        this.lastBypassTick = i;
                    }
                });
            } else {
                this.threshold = 0.0D;
                decreaseVL(0.025D);
            }
        }
    }

    public void setMaxViolation(int vl) {
        if (this.playerData != null && this.playerData.getVersion().afterEq(ClientVersion.V1_9))
            vl = Integer.MAX_VALUE;
        super.setMaxViolation(vl);
    }
}