/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 22:46
 */
package dev.myth.api.utils;

import dev.myth.api.interfaces.IMethods;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.events.MoveEvent;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

@UtilityClass
public class MovementUtil implements IMethods {

    public static final double MIN_DIST = 1.0E-3;
    public static final double BUNNY_DIV_FRICTION = 160.0 - MIN_DIST;
    public static final double[] JUMP_MOTIONS = {
            0.41999998688697815,
            0.33319999363422365,
            0.24813599859094576,
            0.16477328182606651,
            0.08307781780646721,
            0,
            -0.0784000015258789,
            -0.1552320045166016,
            -0.230527368912964,
            -0.30431682745754424,
            -0.37663049823865513,
            -0.44749789698341763
    };

    public double predictMotionY(double motionY) {
        double predictedMotionY = (motionY - 0.08D) * 0.9800000190734863D;
        if(Math.abs(predictedMotionY) < 0.005D) predictedMotionY = 0.0;
        return predictedMotionY;
    }

    public double getBaseMoveSpeed() {
        return getBaseMoveSpeed(false);
    }

    public double getBaseMoveSpeed(boolean ignoreSprinting) {
        double base = MC.thePlayer.isSneaking() ? 0.0663 : (ignoreSprinting || MC.thePlayer.isSprinting() ? 0.2873 : 0.221);
        final PotionEffect moveSpeed = MC.thePlayer.getActivePotionEffect(Potion.moveSpeed);
        final PotionEffect moveSlowness = MC.thePlayer.getActivePotionEffect(Potion.moveSlowdown);
        if (moveSpeed != null) {
            base *= 1.0 + 0.20000000298023224D * (moveSpeed.getAmplifier() + 1);
        }
        if (moveSlowness != null) {
            base *= 1.0 + -0.15000000596046448D * (moveSlowness.getAmplifier() + 1);
        }
        if (MC.thePlayer.isInWater()) {
            base *= 0.52;
        }
        if (MC.thePlayer.isInLava()) {
            base *= 0.52;
        }
        return base;
    }

    public void doLegitJump(MoveEvent event, float yaw) {
        event.setY(0.42F);

        if (MC.thePlayer.isPotionActive(Potion.jump)) {
            event.setY(event.getY() + (double) ((float) (MC.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F));
        }

        if (MC.thePlayer.isSprinting()) {
            float f = yaw * 0.017453292F;
            event.setX(event.getX() - (double) (MathHelper.sin(f) * 0.2F));
            event.setZ(event.getX() + (double) (MathHelper.cos(f) * 0.2F));
        }

        MC.thePlayer.motionX = event.getX();
        MC.thePlayer.motionY = event.getY();
        MC.thePlayer.motionZ = event.getZ();

        MC.thePlayer.isAirBorne = true;
        MC.thePlayer.triggerAchievement(StatList.jumpStat);
    }

    public void fakeJump() {
        MC.thePlayer.isAirBorne = true;
        MC.thePlayer.triggerAchievement(StatList.jumpStat);
    }

    public double getDist(double x, double z, double x2, double z2) {
        return Math.sqrt(Math.pow(x - x2, 2) + Math.pow(z - z2, 2));
    }

    public double getSpeed(MoveEvent event) {
        return Math.sqrt(Math.pow(event.getX(), 2) + Math.pow(event.getZ(), 2));
    }

    public boolean isMoving() {
        return MC.thePlayer.movementInput.moveForward != 0 || MC.thePlayer.movementInput.moveStrafe != 0;
    }

    public static boolean isOnGround(double height) {
        return !MC.theWorld.getCollidingBoundingBoxes(MC.thePlayer, MC.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public boolean isOnGround(){
        return MC.thePlayer.onGround && MC.thePlayer.isCollidedVertically;
    }

    public double getJumpMotion() {
        double motion = 0.42F;
        if (MC.thePlayer.isPotionActive(Potion.jump)) {
            motion += (float) (MC.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }
        return motion;
    }

    public double[] yawPos(double yaw, double dist) {
        return new double[]{-Math.sin(Math.toRadians(yaw)) * dist, Math.cos(Math.toRadians(yaw)) * dist};
    }

    public void setMotion(double speed) {
        if(!isMoving()) return;
        double yaw = Math.toRadians(getDirection());
        MC.thePlayer.motionX = -Math.sin(yaw) * speed;
        MC.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public void setSpeed(MoveEvent event, double speed) {
        if(!isMoving()) return;
        double yaw = Math.toRadians(getDirection());
        event.setX(MC.thePlayer.motionX = -Math.sin(yaw) * speed);
        event.setZ(MC.thePlayer.motionZ = Math.cos(yaw) * speed);
    }

    public float getDirection() {
        float forward = MC.thePlayer.moveForward;
        float strafing = MC.thePlayer.moveStrafing;
        float yaw = MC.thePlayer.rotationYaw;
        return getDirection(forward, strafing, yaw);
    }

    public float getDirection(float forward, float strafing, float yaw) {
        if (forward == 0.0 && strafing == 0.0) return yaw;
        boolean reversed = (forward < 0.0);
        float strafingYaw = 90f * ((forward > 0) ? 0.5f : (reversed ? -0.5f : 1));
        if (reversed) yaw += 180;
        if (strafing > 0) {
            yaw -= strafingYaw;
        } else if (strafing < 0) {
            yaw += strafingYaw;
        }
        return yaw;
    }

    public static double getSpeedDistance() {
        double distX = MC.thePlayer.posX - MC.thePlayer.lastTickPosX;
        double distZ = MC.thePlayer.posZ - MC.thePlayer.lastTickPosZ;
        return Math.sqrt(distX * distX + distZ * distZ);
    }

    public boolean isBlockUnder() {
        for (int offset = 0; offset < MC.thePlayer.posY + MC.thePlayer.getEyeHeight(); offset += 1) {
            AxisAlignedBB boundingBox = MC.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!MC.theWorld.getCollidingBoundingBoxes(MC.thePlayer, boundingBox).isEmpty())
                return true;
        }
        return false;
    }

    public static boolean isInLiquid() {
        return MC.thePlayer.isInWater() || MC.thePlayer.isInLava();
    }

    public void resumeWalk() {
        MC.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(MC.gameSettings.keyBindForward.getKeyCode());
        MC.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(MC.gameSettings.keyBindBack.getKeyCode());
        MC.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(MC.gameSettings.keyBindLeft.getKeyCode());
        MC.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(MC.gameSettings.keyBindRight.getKeyCode());
    }

    public void stopWalk() {
        MC.gameSettings.keyBindForward.pressed = false;
        MC.gameSettings.keyBindBack.pressed = false;
        MC.gameSettings.keyBindLeft.pressed = false;
        MC.gameSettings.keyBindRight.pressed = false;
    }

}
