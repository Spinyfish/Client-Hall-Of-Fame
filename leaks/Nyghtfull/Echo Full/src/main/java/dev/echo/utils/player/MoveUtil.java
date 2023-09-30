package dev.echo.utils.player;

import dev.echo.utils.Utils;
import net.minecraft.util.MathHelper;

public class MoveUtil implements Utils {

    public static boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    public static float getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return (float) Math.toRadians(rotationYaw);
    }

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void strafe(double speed) {
        if (isMoving()) {
            float yaw = getDirection();
            mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
            mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
        }
    }

}