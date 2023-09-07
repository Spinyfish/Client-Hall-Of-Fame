package kevin.module.modules.movement;

import kevin.event.*;
import kevin.module.BooleanValue;
import kevin.module.Module;
import kevin.module.ModuleCategory;
import kevin.utils.RotationUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import static kevin.utils.RotationUtils.targetRotation;

public class MoveCorrection extends Module {
    public static MoveCorrection INSTANCE;
    private float fixedYaw = 0f;
    private boolean fixed = false;

    public MoveCorrection() {
        super("MoveCorrection", "Make your movement with rotation follow the vanilla protocol", Keyboard.KEY_NONE, ModuleCategory.MOVEMENT);
        INSTANCE = this;
    }

    @EventTarget
    public void onStrafe(final MovementInputUpdateEvent event) {
        if (targetRotation == null) return;
        final float forward = event.getForward();
        final float strafe = event.getStrafe();
        final float yaw = fixedYaw = targetRotation.getYaw();
        fixed = true;

        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(mc.thePlayer.rotationYaw, forward, strafe)));

        if (forward == 0 && strafe == 0) {
            return;
        }

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1f; predictedForward <= 1f; predictedForward += 1f) {
            for (float predictedStrafe = -1f; predictedStrafe <= 1f; predictedStrafe += 1f) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                final double difference = Math.abs(angle - predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    @EventTarget
    public void onStrafe(StrafeEvent event) {
        if (fixed) {
            fixed = false;
            event.setYaw(fixedYaw);
        }
    }

    @EventTarget
    public void onJump(JumpEvent event) {
        if (fixed) {
            event.setYaw(fixedYaw);
        }
    }

    /**
     * Gets the players' movement yaw
     */
    public double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
}
