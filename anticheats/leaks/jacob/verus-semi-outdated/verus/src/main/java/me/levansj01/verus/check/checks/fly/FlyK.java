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
        subType = "K",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -2.0D,
        maxViolations = 10,
        logData = true
)
public class FlyK extends MovementCheck {
    public void handle(PlayerLocation location1, PlayerLocation location2, long var3) {
        double y = location1.getY() - location2.getY();
        if (location1.getGround() && y > 0.079D && !this.playerData.isFlying() && this.playerData.isSurvival()) {
            if (!this.playerData.isTeleporting(2)) {
                int ticker = this.playerData.getTickerMap().get(TickerType.TELEPORT);
                if (ticker > 1) {
                    ticker = this.playerData.getVelocityTicks();
                    int ping = this.playerData.getPingTicks();
                    if (ticker > ping / 2 && this.playerData.isSpawned() && !this.playerData.hasLag() && !this.playerData.isFallFlying() && !this.playerData.isGliding() && Math.abs(location1.getY() % 0.5D - 0.015555072702202466D) > 1.0E-12D) {
                        if (!this.playerData.hasPlacedBlock(true)) {
                            World world = this.player.getWorld();
                            Cuboid cuboid = (new Cuboid(this.playerData.getLocation())).add(-0.5D, 0.5D, -1.0D, 1.5D, -0.5D, 0.5D);
                            this.run(() -> {
                                if (cuboid.checkBlocks(this.player, world, (w) -> !MaterialList.BAD_VELOCITY.contains(w) && !MaterialList.INVALID_SHAPE.contains(w))) {
                                    this.handleViolation(String.format("D: %s G: %s", y, location2.getGround()));
                                }
                            });
                            return;
                        }
                    }
                }
            }
        }
        this.decreaseVL(0.005D);
    }
}
