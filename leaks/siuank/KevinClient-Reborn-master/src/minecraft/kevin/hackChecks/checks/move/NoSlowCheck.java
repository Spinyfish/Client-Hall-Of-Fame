package kevin.hackChecks.checks.move;

import kevin.hackChecks.Check;
import net.minecraft.client.entity.EntityOtherPlayerMP;

// NoSlowCheck detect player who is usingItem when sprinting
// It will not false anytime, I don't know.
public class NoSlowCheck extends Check {
    short sprintBuffer = 0, motionBuffer = 0;
    public NoSlowCheck(EntityOtherPlayerMP playerMP) {
        super(playerMP);
        name = "NoSlow";
        checkViolationLevel = 20;
    }

    @Override
    public void onLivingUpdate() {
        if (handlePlayer.isUsingItem() || handlePlayer.isBlocking()) {
            if (handlePlayer.isSprinting()) {
                if (++sprintBuffer > 5) {
                    flag("Sprinting when using item or blocking", 3);
                }
                return;
            }
            // a motion check
            double dx = handlePlayer.prevPosX - handlePlayer.posX, dz = handlePlayer.prevPosZ - handlePlayer.posZ;
            if (dx * dx + dz * dz > 0.0625) { // sq: 0.25
                if (++motionBuffer > 10) {
                    flag("NoSprint but keep in sprint motion when blocking", 3);
                    motionBuffer = 7;
                    return;
                }
            }
            motionBuffer -= motionBuffer > 0 ? 1 : 0;
            sprintBuffer -= sprintBuffer > 0 ? 1 : 0;
            if (sprintBuffer < 2) {
                reward();
            }
        }
    }

    @Override
    public String description() {
        return "using item and moving suspiciously";
    }
}
