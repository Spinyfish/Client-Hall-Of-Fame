package kevin.hackChecks.checks.move;

import kevin.hackChecks.Check;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

// I don't really want to make a speed check
// because it is too difficult for client
// we may not get target's correct position and effects
// calculate friction? it is almost impossible
// but I tried to make it
public class SpeedCheck extends Check {
    public SpeedCheck(EntityOtherPlayerMP playerMP) {
        super(playerMP);
        checkViolationLevel = 30;
    }

    boolean updated = false, lastOnGround = false;
    double lastXZ = 666;
    short buffer = 0, strafeBuffer = 0;

    double offGroundXSpeed = 0, offGroundZSpeed = 0;

    @Override
    public void onLivingUpdate() {
        double x = handlePlayer.posX - handlePlayer.prevPosX;
        double z = handlePlayer.posZ - handlePlayer.prevPosZ;
        final double xzSq = x * x + z * z;
        final double xz = Math.sqrt(xzSq);
        if (updated) {
            if (!handlePlayer.onGround) {
                if (lastOnGround) {
                    offGroundXSpeed = x;
                    offGroundZSpeed = z;
                }
                double dir = Math.atan2(-x, -z) * 180.0 / Math.PI;
                double gDir = Math.atan2(-offGroundXSpeed, -offGroundZSpeed) * 180.0 / Math.PI;
                double diff = Math.abs(dir - gDir);
                if (diff > 30 && diff < 330 /* 360 - 30 */) {
                    if (xz >= 0.375) strafeBuffer += 4;
                    if (strafeBuffer >= 60) {
                        flag(String.format("invalid strafe movement, (ad,d,dg)=(%.3f,%.3f,%.3f) buffer=%s", diff, dir, gDir, strafeBuffer), 2.5);
                        strafeBuffer -= 4;
                    }
                } else strafeBuffer -= strafeBuffer > 0 ? 1 : 0;
            }
            if (handlePlayer.isPotionActive(Potion.moveSpeed) && (handlePlayer.onGround || lastOnGround)) { // don't check when the player have the potion speed
                updated = false;
                lastXZ = xz;
                return;
            }
            double predict = lastXZ * 0.91, allowed = 0.026;
            if (lastOnGround) {
                // should we?
                predict *= mc.theWorld.getBlockState(new BlockPos(handlePlayer.prevPosX, handlePlayer.prevPosY - 1, handlePlayer.prevPosZ)).getBlock().slipperiness;
                if (!handlePlayer.onGround) predict += 0.2;
            }
            double outed = xz - predict, ratio = xz / (predict + allowed);
            outed -= allowed;
            if (outed >= 1e-5) {
                buffer += 2;
                if (ratio >= 2.0) { // flying maybe, it moved too fast
                    buffer += 2;
                }
                if (buffer > 30) {
                    flag("speed up movement, o=" + outed + ",b=" + buffer + ",r=" + ratio, 1.2);
                    buffer -= 5;
                }
            } else {
                buffer -= buffer > 0 ? 1 : 0;
                reward(0.05);
            }
            updated = false;
        }
        lastXZ = xz;
        lastOnGround = handlePlayer.onGround;
    }

    @Override
    public void positionUpdate(double x, double y, double z) {
        updated = true;
    }

    @Override
    public String description() {
        return "move horizontal suspiciously";
    }

    @Override
    public void reset() {
        super.reset();
        buffer = 0;
        strafeBuffer = 0;
    }

    @Override
    public String reportName() {
        return "speed";
    }
}
