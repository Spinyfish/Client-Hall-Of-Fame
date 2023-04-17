package me.levansj01.verus.check.checks.fly;

import java.util.List;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

@CheckInfo(
        type = CheckType.FLY,
        subType = "B",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -5.0,
        maxViolations = 25
)
public class FlyB extends MovementCheck {
    private int lastBypassTick = -10;
    private int threshold;

    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation1, long l) {
        if (playerLocation.getGround() && playerLocation1.getGround()
                && playerLocation.getY() != playerLocation1.getY() && !MathUtil.onGround(playerLocation.getY())) {
            int[] totalTicks = {this.playerData.getTotalTicks()};
            if (totalTicks[0] > 200L && !MathUtil.onGround(playerLocation1.getY()) && this.playerData.isSpawned()) {
                totalTicks[0] = this.playerData.getTotalTicks();
                if (totalTicks[0] - 10L > this.lastBypassTick && !this.playerData.canFly() && !this.playerData.isFallFlying()
                        && !this.playerData.isGliding() && !this.playerData.isLevitating() && !this.playerData.isFlying() && !this.playerData.hadJumpBoost()) {
                    World world = this.player.getWorld();
                    Cuboid cuboid = (new Cuboid(this.playerData.getLocation())).expand(0.5, 0.5, 0.5);
                    double deltaY = playerLocation1.getY() - playerLocation.getY();
                    int ticks = this.playerData.getTotalTicks();
                    this.run(() -> {
                        if (!cuboid.checkBlocks(this.player, world, material -> (NMSManager.getInstance().getServerVersion().before(ServerVersion.v1_11_R1)
                                || material != MaterialList.PURPLE_FUCKING_SHULKER)
                                && !MaterialList.INVALID_SHAPE.contains(material))) {
                            this.threshold = 0;
                            this.violations -= Math.min(this.violations + 4.0, 0.05);
                            this.lastBypassTick = ticks;
                        } else {

                            List<Entity> entities = this.player.getNearbyEntities(2.5, 2.5, 2.5);
                            int i = 0;

                            for (Entity entity : entities) {
                                i++;
                                if (i >= entities.size()) {
                                    this.threshold += 1;
                                    this.handleViolation(String.format("D: %s", deltaY), this.threshold);
                                } else {
                                    if (entity instanceof Boat || entity instanceof Minecart || entity.getType().name().equalsIgnoreCase("SHULKER")) {
                                        this.threshold = 0;
                                        this.lastBypassTick = totalTicks[0] - 100;
                                        this.violations -= Math.min(this.violations + 4.0, 0.05);
                                    }
                                }
                            }
                        }

                    });

                    return;
                }
            }
        }

        this.threshold = 0;
        this.violations -= Math.min(this.violations + 4.0, 0.05);
    }

}
