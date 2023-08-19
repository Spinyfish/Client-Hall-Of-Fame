/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:17
 */

package cc.swift.util.player;

import cc.swift.Swift;
import cc.swift.module.impl.player.MovementCorrectionModule;
import cc.swift.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;

@UtilityClass
public class MovementUtil implements IMinecraft {

    public double getJumpMotion() {
        return 0.42f + (mc.thePlayer.isPotionActive(Potion.jump) ? (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f : 0);
    }

    public float getDirection() {
        float yaw = mc.thePlayer.rotationYaw;
        if (Swift.INSTANCE.getModuleManager().getModule(MovementCorrectionModule.class).isEnabled())
            yaw = Swift.INSTANCE.getRotationHandler().getYaw();
        return getDirection(mc.thePlayer.moveForward, mc.thePlayer.moveStrafing, yaw);
    }

    public float getDirection(float forward, float strafing, float yaw) {
        if (forward == 0.0 && strafing == 0.0) return yaw;
        boolean reversed = forward < 0.0;
        float strafingYaw = 90f * (forward > 0 ? 0.5f : (reversed ? -0.5f : 1));
        if (reversed) yaw += 180;
        if (strafing > 0) {
            yaw -= strafingYaw;
        } else if (strafing < 0) {
            yaw += strafingYaw;
        }
        return yaw;
    }

    public double[] yawPos(double yaw, double dist) {
        return new double[]{-Math.sin(Math.toRadians(yaw)) * dist, Math.cos(Math.toRadians(yaw)) * dist};
    }

    public double getSpeed() {
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0;
    }

    public boolean isOnGround() {
        return mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
    }

    public void setSpeed(double speed) {
        double[] dir = yawPos(getDirection(), speed);
        mc.thePlayer.motionX = dir[0];
        mc.thePlayer.motionZ = dir[1];
    }

    public void addSpeed(double speed) {
        double[] dir = yawPos(getDirection(), speed);
        mc.thePlayer.motionX += dir[0];
        mc.thePlayer.motionZ += dir[1];
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
        }

        return baseSpeed;
    }

    public static float getBlocksPerSecond() {
        return (float) Math.round(mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ) * 20D * 100) / 100;
    }

}
