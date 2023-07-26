package dev.hera.client.utils;

import net.minecraft.client.Minecraft;

public class MovementUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static float getDirection() {
        float yaw = mc.thePlayer.rotationYaw;
        float forward = 1f;

        if(mc.thePlayer.moveForward < 0f)
            yaw += 180f;

        if(mc.thePlayer.moveForward < 0f)
            forward = -0.5f;
        else if(mc.thePlayer.moveForward > 0f)
            forward = 0.5f;

        if(mc.thePlayer.moveStrafing > 0f)
            yaw -= 90 * forward;
        else if(mc.thePlayer.moveStrafing < 0f)
            yaw += 90f * forward;

        yaw *= 0.017453292F;
        return yaw;

    }

    public static boolean isMoving(){
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0f || mc.thePlayer.movementInput.moveStrafe != 0f);
    }

    public static void strafe(double speed) {
        if(!isMoving())
            return;
        double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;

    }

}