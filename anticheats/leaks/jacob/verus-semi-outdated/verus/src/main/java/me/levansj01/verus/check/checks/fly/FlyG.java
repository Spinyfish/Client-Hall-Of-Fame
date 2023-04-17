package me.levansj01.verus.check.checks.fly;

import java.util.concurrent.ThreadLocalRandom;
import me.levansj01.verus.alert.manager.AlertManager;
import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.World;

@CheckInfo(
        type = CheckType.FLY,
        subType = "G",
        friendlyName = "Fly",
        version = CheckVersion.RELEASE,
        minViolations = -2.5,
        maxViolations = 20,
        logData = true
)
public class FlyG extends MovementCheck {

    private boolean ignoring = false;
    private int jump = 0;

    @Override
    public void handle(PlayerLocation playerLocation, PlayerLocation playerLocation2, long l) {
        if (this.ignoring) {
            if (playerLocation2.getGround()) {
                this.ignoring = false;
            }
        } else if (playerLocation2.getY() > playerLocation.getY()
                && this.playerData.getVelocityTicks() > (this.playerData.getPingTicks() + 1) * 2
                && !this.playerData.isVehicle() && !this.playerData.canFly() && !this.playerData.isFallFlying()
                && !this.playerData.isGliding() && !this.playerData.isRiptiding() && !this.playerData.isHooked()
                && !this.playerData.isTeleportingV2() && this.playerData.isSurvival() && this.playerData.isSpawned()
                && !this.playerData.isLevitating() && !this.playerData.hasPlacedBlock(true)
                && !this.playerData.hadJumpBoost() && !this.playerData.hadLevitation()) {
            double d1 = playerLocation2.getY() - Math.max(0.0D, playerLocation.getY());
            if (d1 > 100000.0D || this.violations > 300.0D) {
                AlertManager.getInstance().handleBan(this.playerData, this, false);
                this.playerData.fuckOff();
            }
            int i = (Integer) this.playerData.getJumpLevel().get();
            double d2 = 0.41999998688699D;
            if (!playerLocation2.getGround()) {
            } else {
            }
            double d3 = Math.max(0.5D, d2 + Math.max(this.jump, i) * 0.2D);
            double d4 = d1 - d3;
            if (playerLocation.getGround())
                this.jump = i;
            if (this.playerData.getVersion() != ClientVersion.V1_7 && playerLocation2.getGround()
                    && playerLocation.getGround() && (d4 == 0.0625D || d4 == 0.10000002384185791D))
                return;
            if (d1 > d3 && Math.abs(d1 - 0.5D) > 1.0E-12D) {
                if (this.playerData.isTeleporting(2) && !this.playerData.isTeleportingV2()) {
                    this.playerData.setLastTransactionID((short)(ThreadLocalRandom.current().nextInt(65532) + -32768));
                    this.playerData.setSpawned(2147483647);
                }
                World world = this.player.getWorld();
                Cuboid cuboid = (new Cuboid(playerLocation)).move(0.0D, -1.5D, 0.0D).expand(0.5D, 2.0D, 0.5D);
                run(() -> {
                    if (cuboid.checkBlocks(this.player, world, material -> {
                        if (!MaterialList.INVALID_JUMP.contains(material) && !MaterialList.SHULKER_BOX.contains(material)
                                && material != MaterialList.PURPLE_FUCKING_SHULKER) {
                        } else {

                        }
                        return false;
                    })) {
                        if (this.playerData.isTeleporting()) {
                        } else {

                        }
                        handleViolation(String.format("D: %s", d4), Math.min(10.0D, 0.5D + d4));
                    } else {
                        this.ignoring = true;
                    }
                });
            } else {
                this.violations -= Math.min(this.violations + 2.5D, 0.025D);
            }
        }
    }
}
