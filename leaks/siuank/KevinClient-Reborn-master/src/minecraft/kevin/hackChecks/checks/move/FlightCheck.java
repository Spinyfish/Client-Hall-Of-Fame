package kevin.hackChecks.checks.move;

import kevin.hackChecks.Check;
import kevin.utils.OtherExtensionsKt;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.List;

// FlightCheck detect the player who is floating / flying / gliding
// It check for motionY
// All players' motionY is changed as (lastMotionY - 0.08D) * 0.98F  in air
// but in client, it is hard to check player is flying because we cannot get others' accurate location

// so we just make a simple check for this problem to detect floating hack
public class FlightCheck extends Check {
    // use buffer to prevent false positives.
    // we are not the antiCheat for server, we haven't promised to mark all cheaters.
    // it likes VerusAC, work silent and accurate.
    // there is no meaningful of this check if we have lots of false
    short buffer = 0, jumpBuffer = 0;
    public FlightCheck(EntityOtherPlayerMP playerMP) {
        super(playerMP);
        name = "Flight";
        checkViolationLevel = 25;
    }
    private double lastDeltaY = 0;

    @Override
    public void positionUpdate(double x, double y, double z) {
        if (handlePlayer.capabilities.isFlying) {
            lastDeltaY = y;
            return;
        }
        // Float / Fly up
        if (y >= lastDeltaY && !(handlePlayer.isInWater() || handlePlayer.isInLava())) {
//                BlockPos bp = new BlockPos(handlePlayer.prevPosX, handlePlayer.prevPosY - 0.25, handlePlayer.prevPosZ);
            // "strict" ground check
            AxisAlignedBB aabb = new AxisAlignedBB((handlePlayer.serverPosX / 32.0) - 0.30125, (handlePlayer.serverPosY / 32.0) - 0.5, (handlePlayer.serverPosZ / 32.0) - 0.30125, (handlePlayer.serverPosX / 32.0) + 0.30125, (handlePlayer.serverPosY / 32.0) + 0.25, (handlePlayer.serverPosZ / 32.0) + 0.30125);
            if (OtherExtensionsKt.getBlockStatesIncluded(aabb).isEmpty()) { // No block found under the player
                if (handlePlayer.hurtTime < 3 && ++buffer > 5) {
                    flag(String.format("glide/fly, d=(%.5f, %.5f), b=%s", lastDeltaY, y, buffer), 1.3);
                    if (buffer > 10) buffer = 10;
                }
            } else {
                --buffer;
            }
        }

        // Fast down (code from MedusaAntiCheat and better value for client side)
        if (y < -4.0) {
            flag("fast down, mY=" + y, 5);
        }

        // High jump
        double jumpMotion = 0.42;
        if (handlePlayer.isPotionActive(Potion.jump)) {
            jumpMotion += (handlePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;
        }
        if (y > jumpMotion) {
            // A buffer to prevent false
            // yes, it will only flag which player is over 0.5 motion jump or 0.42 jump but slow down,
            // so it will no false any player unless the connection is trash
            // false when step? yes if we don't use buffer, but we buffer it
            if (++jumpBuffer > 1) {
                flag("high jump, p=(" + jumpMotion + "," + y + ")", 4.5);
                jumpBuffer = 0;
            }
        } else jumpBuffer = 0; // --buffer?

        reward(0.1);
        lastDeltaY = y;
    }

    @Override
    public String description() {
        return "move vertical suspiciously";
    }

    @Override
    public double getPoint() {
        return 2.23;
    }

    @Override
    public String reportName() {
        return "fly";
    }
}
