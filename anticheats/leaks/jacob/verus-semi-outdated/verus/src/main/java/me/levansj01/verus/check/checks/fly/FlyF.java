package me.levansj01.verus.check.checks.fly;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.location.PlayerLocation;
import me.levansj01.verus.verus2.data.player.TickerType;
import org.bukkit.World;

@CheckInfo(
        type = CheckType.FLY,
        subType = "F",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -5.0,
        maxViolations = 100
)
public class FlyF extends MovementCheck {

    private Double lastYChange = null;
    private int lastBypassTick = -10;
    private int threshold;

    public void handle(PlayerLocation location, PlayerLocation location2, long l) {
        if (!this.playerData.canFly() && this.playerData.isSurvival() && !location2.getGround() && !location.getGround()
                && this.playerData.getTotalTicks() - 10 > this.lastBypassTick && !this.playerData.isTeleporting(3)
                && this.playerData.getTickerMap().get(TickerType.TELEPORT) > 2 && !this.playerData.isVehicle()
                && this.playerData.isSpawned() && !this.playerData.isLevitating() && !this.playerData.isFallFlying() && !this.playerData.isGliding()) {
            double deltaY = Math.abs(location.getY() - location2.getY());
            if (this.lastYChange != null && deltaY > 0.0 && location.getY() > 0.0 && location2.getY() > 0.0 && !this.playerData.hasFast()) {
                if (deltaY == this.lastYChange && (deltaY < 0.098 || deltaY > 0.09800001)
                        && Math.abs(deltaY % 0.5) - 0.15523200451660557 > 1.0E-12 && Math.abs(deltaY % 0.5) - 0.23052736891296366 > 1.0E-12) {
                    World world = this.player.getWorld();
                    Cuboid cuboid = (new Cuboid(location2)).add(-0.5, 0.5, -0.5, 1.5, -0.5, 0.5);
                    Cuboid cuboid2 = Cuboid.withLimit(location, location2, 16).move(0.0, 2.0, 0.0).expand(0.29999, 0.5, 0.29999);
                    int totalTicks = this.playerData.getTotalTicks();
                    if (deltaY == 1.0999999999999943) {
                        this.lastBypassTick = totalTicks;
                        return;
                    }
                    this.run(() -> {
                        if (cuboid.checkBlocks(this.player, world, material -> !MaterialList.BAD_VELOCITY.contains(material))
                                && cuboid2.checkBlocks(this.player, world, material -> material == MaterialList.AIR)) {
                            if (deltaY % 0.01 == 0.0) {
                                ++this.threshold;
                            }
                            if (this.threshold++ > 1) {
                                this.handleViolation(String.format("D: %s", deltaY), (double)this.threshold / 2.0);
                            }
                        } else {
                            this.threshold = 0;
                            this.violations -= Math.min(this.violations + 5.0, 0.01);
                            this.lastBypassTick = totalTicks;
                        }
                    });
                } else {
                    this.violations -= Math.min(this.violations + 5.0, 0.01);
                    this.threshold = 0;
                }
            }
            this.lastYChange = deltaY;
        }
    }
}