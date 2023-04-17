package me.levansj01.verus.check.checks.fly;

import me.levansj01.verus.check.MovementCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.compat.ServerVersion;
import me.levansj01.verus.data.version.ClientVersion;
import me.levansj01.verus.util.BukkitUtil;
import me.levansj01.verus.util.Cuboid;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.MathHelper;
import me.levansj01.verus.util.java.MathUtil;
import me.levansj01.verus.util.location.PlayerLocation;
import org.bukkit.World;
import org.bukkit.util.Vector;

@CheckInfo(
        type = CheckType.FLY,
        subType = "Z",
        friendlyName = "Elytra Fly",
        version = CheckVersion.RELEASE,
        minViolations = -7.5,
        unsupportedVersions = {ClientVersion.V1_7, ClientVersion.V1_8},
        unsupportedServers = {ServerVersion.v1_7_R4, ServerVersion.v1_8_R3}
)
public class FlyZ extends MovementCheck {
    
    private int lastBypass;
    private int ticks = 0;
    private final Vector lastMovement = new Vector();
    private static final float VALUE = 0.017453292F;

    @Override
    public void handle(PlayerLocation location, PlayerLocation location2, long l) {
        if (!this.playerData.isFallFlying() || this.playerData.canFly() || this.playerData.isBoosting() || location2.getGround() || this.playerData.isLevitating() || this.playerData.getVelocityTicks() <= this.playerData.getMaxPingTicks() || location.getGround() || this.playerData.getTotalTicks() - this.lastBypass <= 20 || !NMSManager.getInstance().getServerVersion().before(ServerVersion.v1_13_R2) && BukkitUtil.hasEffect(this.playerData.getEffects(), 28)) {
            this.ticks = 0;
        } else {
            if (this.ticks++ > 9) {
                float pitch = location2.getPitch();
                float yaw = location2.getYaw();
                float mPitch = pitch * 0.017453292F;
                double deltaY = location2.getY() - location.getY();
                double lastMoveY = this.lastMovement.getY();
                double lastMoveLen = this.lastMovement.length();
                Vector rotateVector = getVectorForRotation(pitch, yaw);
                double vectorLen = rotateVector.length();
                double d = Math.sqrt(Math.pow(rotateVector.getX(), 2.0) + Math.pow(rotateVector.getZ(), 2.0));
                float f = (float)(Math.pow(MathHelper.cos(mPitch), 2.0) * Math.min(1.0, vectorLen / 0.4));
                double d1 = deltaY / 0.9900000095367432;
                double d2 = (lastMoveY - 0.08) * 0.9800000190734863;
                if (mPitch < 0.0F) {
                    d1 -= lastMoveLen * (double)(-MathHelper.sin(mPitch)) * 0.04 * 3.2;
                }

                if (lastMoveY < 0.0 && d > 0.0) {
                    d1 -= lastMoveY * -0.1 * (double)f;
                }

                if (this.playerData.getVersion().before(ClientVersion.V1_13)) {
                    d1 -= -0.08 + (double)f * 0.06;
                } else {
                    d1 -= 0.08 * (-1.0 + (double)f * 0.75);
                }

                double lastMoveLow = MathUtil.lowestAbs(lastMoveY - d1, deltaY - d2);
                double lowest = MathUtil.lowestAbs((d1 - lastMoveY) / d1, (deltaY - d2) / d2);
                if (Math.abs(lastMoveLow) > 0.025 && Math.abs(lowest) > 0.075) {
                    World world = this.player.getWorld();
                    Cuboid cuboid = (new Cuboid(location2)).expand(0.5, 1.0, 0.5).move(0.0, 1.0, 0.0);
                    int totalTicks = this.playerData.getTotalTicks();
                    this.run(() -> {
                        if (this.playerData.getTotalTicks() - this.lastBypass >= 20) {
                            if (cuboid.checkBlocks(this.player, world, material -> !MaterialList.BAD_VELOCITY.contains(material))) {
                                this.handleViolation(() -> String.format("%.2f %.2f", deltaY, lastMoveLow));
                            } else {
                                this.lastBypass = totalTicks;
                                this.ticks = 0;
                            }

                        }
                    });
                } else {
                    this.decreaseVL(0.1);
                }
            }

            this.lastMovement.setX(location2.getX() - location.getX());
            this.lastMovement.setY(location2.getY() - location.getY());
            this.lastMovement.setZ(location2.getZ() - location.getZ());
        }

    }

    private static Vector getVectorForRotation(float f, float f1) {
        float location2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float var3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float var4 = -MathHelper.cos(-f * 0.017453292F);
        float pitch = MathHelper.sin(-f * 0.017453292F);
        return new Vector(var3 * var4, pitch, location2 * var4);
    }
}
