package me.levansj01.verus.check.checks.speed;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.util.MutableBlockLocation;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.Material;
import org.bukkit.World;

@CheckInfo(
        type = CheckType.SPEED,
        subType = "B",
        friendlyName = "FastLadder",
        version = CheckVersion.RELEASE,
        maxViolations = 25,
        logData = true
)
public class SpeedB extends MovementCheck {

    private double lastDiff;
    private int lastLadderCheck;
    private int threshold;
    private boolean ladder = false;

    public SpeedB() {
    }

    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        if (playerLocation2.getY() > playerLocation.getY() && !playerLocation2.getGround() 
                && !playerLocation.getGround() && !this.playerData.isFlying()
                && this.playerData.getVelocityTicks() > this.playerData.getPingTicks() * 2
                && !this.playerData.isLevitating()) {
            if (this.ladder) {
                double deltaY = playerLocation2.getY() - playerLocation.getY();
                if (deltaY > 0.118) {
                    if (this.threshold++ > 1 && deltaY >= this.lastDiff * 0.95) {
                        this.threshold = 0;
                        PlayerLocation locationClone = playerLocation2.clone();
                        World world = this.player.getWorld();
                        this.run(() -> {
                            MutableBlockLocation blockLocation = new MutableBlockLocation((int)Math.floor(locationClone.getX()),
                                    (int)Math.floor(locationClone.getY() + 1.0), (int)Math.floor(locationClone.getZ()));
                            Material material = NMSManager.getInstance().getType(this.player, world, blockLocation);
                            boolean notEligible = blockLocation.isWaterLogged(world) || blockLocation.add(0, -1, 0).isWaterLogged(world)
                                    || blockLocation.add(0, 2, 0).isWaterLogged(world);
                            if (!notEligible && MaterialList.CLIMBABLE.contains(material)) {
                                this.handleViolation(String.format("D: %s", deltaY));
                            } else {
                                this.ladder = false;
                            }

                        });
                    }
                } else {
                    this.threshold = 0;
                }

                this.lastDiff = deltaY;
            } else if (this.lastLadderCheck++ > 9) {
                this.lastLadderCheck = 0;
                PlayerLocation location = playerLocation2.clone();
                World world = this.player.getWorld();
                this.run(() -> {
                    MutableBlockLocation blockLocation = new MutableBlockLocation((int)Math.floor(location.getX()),
                            (int)Math.floor(location.getY() + 1.0), (int)Math.floor(location.getZ()));
                    Material material = NMSManager.getInstance().getType(this.player, world, blockLocation);
                    if (MaterialList.CLIMBABLE.contains(material)) {
                        this.ladder = true;
                    }

                });
            }
        } else {
            this.ladder = false;
        }

    }
}
