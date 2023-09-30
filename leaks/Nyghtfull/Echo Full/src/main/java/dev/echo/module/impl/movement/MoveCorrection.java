package dev.echo.module.impl.movement;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.JumpEvent;
import dev.echo.listener.event.impl.player.StrafeEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.utils.player.AlwaysUtil;
import net.minecraft.util.MathHelper;

public class MoveCorrection extends Module {
    private float fixedYaw = 0f;
    private boolean fixed = false;

    public MoveCorrection() {
        super("MoveCorrection", Category.MOVEMENT, "Silent Move Fix");
    }

    @Link
    public Listener<StrafeEvent> strafeEventListener = event -> {
        if (AlwaysUtil.isPlayerInGame()) {
            final float forward = event.getForward();
            final float strafe = event.getStrafe();
            final float yaw = fixedYaw = event.getYaw();
            fixed = true;

            final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(mc.thePlayer.rotationYaw, forward, strafe)));

            if (forward == 0 && strafe == 0) {
                return;
            }

            float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

            for (float predictedForward = -1f; predictedForward <= 1f; predictedForward += 1f) {
                for (float predictedStrafe = -1f; predictedStrafe <= 1f; predictedStrafe += 1f) {
                    if (predictedStrafe == 0 && predictedForward == 0) {
                        continue;
                    }

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
            if (fixed) {
                fixed = false;
                event.setYaw(fixedYaw);
            }
        }
    };
    @Link
    private final Listener<JumpEvent> jumpEventListener = (event) -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (fixed) {
            event.setYaw(fixedYaw);
        }
    };

    public double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) {
            rotationYaw += 180F;
        }

        float forward = 1F;

        if (moveForward < 0F) {
            forward = -0.5F;
        } else if (moveForward > 0F) {
            forward = 0.5F;
        }

        if (moveStrafing > 0F) {
            rotationYaw -= 90F * forward;
        }
        if (moveStrafing < 0F) {
            rotationYaw += 90F * forward;
        }

        return Math.toRadians(rotationYaw);
    }
}
