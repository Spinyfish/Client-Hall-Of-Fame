package dev.thread.api.util.player;

import dev.thread.api.util.IWrapper;

public class MoveUtil implements IWrapper {

    private static float getRotations() {
        float newYaw = mc.thePlayer.rotationYaw;
        float strafe = mc.thePlayer.moveForward < 0 ? -45 : 45;

        if (mc.thePlayer.moveForward < 0)
            newYaw += 180;

        if (mc.thePlayer.moveStrafing > 0)
            newYaw -= strafe;
        else if (mc.thePlayer.moveStrafing < 0)
            newYaw += strafe;

        if (mc.thePlayer.moveForward == 0)
            newYaw += (mc.thePlayer.moveStrafing > 0) ? -45 : 45;

        return newYaw;
    }

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static double getBlocksPerSecond() {
        return Math.abs(Math.hypot(mc.thePlayer.posX - mc.thePlayer.lastTickPosX, mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ)) * 20 * mc.timer.timerSpeed;
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static void strafe(double speed) {
        if (isPlayerMoving()) {
            double yaw = Math.toRadians(getRotations());

            mc.thePlayer.motionZ = Math.cos(yaw) * speed;
            mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        }
    }

    public static boolean isPlayerMoving() {
        return mc.thePlayer.moveStrafing != 0 || mc.thePlayer.moveForward != 0;
    }



}
