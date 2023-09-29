// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.utils;

import org.lwjgl.input.Keyboard;
import today.getbypass.events.Event;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.client.Minecraft;

public class MoveUtil
{
    public static Minecraft mc;
    
    static {
        MoveUtil.mc = Minecraft.getMinecraft();
    }
    
    public static boolean isMovingOnGround() {
        return isMoving() && MoveUtil.mc.thePlayer.onGround;
    }
    
    public static double getJumpHeight(final double speed) {
        return MoveUtil.mc.thePlayer.isPotionActive(Potion.jump) ? (speed + 0.1 * (MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1)) : speed;
    }
    
    public static void strafe(final float speed) {
        if (!isMoving()) {
            return;
        }
        final double yaw = getDirection();
        MoveUtil.mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        MoveUtil.mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }
    
    public static double getDirection() {
        float rotationYaw = MoveUtil.mc.thePlayer.rotationYaw;
        if (MoveUtil.mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (MoveUtil.mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        }
        else if (MoveUtil.mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (MoveUtil.mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (MoveUtil.mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }
    
    public static void sendPosition(final double x, final double y, final double z, final boolean ground, final boolean moving) {
        if (!moving) {
            MoveUtil.mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(MoveUtil.mc.thePlayer.posX, MoveUtil.mc.thePlayer.posY + y, MoveUtil.mc.thePlayer.posZ, ground));
        }
        else {
            MoveUtil.mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(MoveUtil.mc.thePlayer.posX + x, MoveUtil.mc.thePlayer.posY + y, MoveUtil.mc.thePlayer.posZ + z, ground));
        }
    }
    
    public static Block getBlockAtPos(final BlockPos inBlockPos) {
        final IBlockState s = MoveUtil.mc.theWorld.getBlockState(inBlockPos);
        return s.getBlock();
    }
    
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2875;
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }
    
    public static boolean isMoving() {
        return MoveUtil.mc.thePlayer.movementInput.moveForward != 0.0f || MoveUtil.mc.thePlayer.movementInput.moveStrafe != 0.0f;
    }
    
    public static double defaultMoveSpeed() {
        return MoveUtil.mc.thePlayer.isSprinting() ? 0.28700000047683716 : 0.22300000488758087;
    }
    
    public static double getLastDistance() {
        return Math.hypot(MoveUtil.mc.thePlayer.posX - MoveUtil.mc.thePlayer.prevPosX, MoveUtil.mc.thePlayer.posZ - MoveUtil.mc.thePlayer.prevPosZ);
    }
    
    public static boolean isOnGround(final double height) {
        return !MoveUtil.mc.theWorld.getCollidingBoundingBoxes(MoveUtil.mc.thePlayer, MoveUtil.mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }
    
    public static double jumpHeight() {
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.jump)) {
            return 0.419999986886978 + 0.1 * (MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1);
        }
        return 0.419999986886978;
    }
    
    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (MoveUtil.mc.thePlayer.isPotionActive(Potion.jump)) {
            final int amplifier = MoveUtil.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (amplifier + 1) * 0.1f;
        }
        return baseJumpHeight;
    }
    
    public static void setSpeed(final double moveSpeed, float yaw, double strafe, double forward) {
        final double fforward = MoveUtil.mc.thePlayer.movementInput.moveForward;
        final double sstrafe = MoveUtil.mc.thePlayer.movementInput.moveStrafe;
        final float yyaw = MoveUtil.mc.thePlayer.rotationYaw;
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            }
            else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0;
            if (forward > 0.0) {
                forward = 1.0;
            }
            else if (forward < 0.0) {
                forward = -1.0;
            }
        }
        if (strafe > 0.0) {
            strafe = 1.0;
        }
        else if (strafe < 0.0) {
            strafe = -1.0;
        }
        final double mx = Math.cos(Math.toRadians(yaw + 90.0f));
        final double mz = Math.sin(Math.toRadians(yaw + 90.0f));
        MoveUtil.mc.thePlayer.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        MoveUtil.mc.thePlayer.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }
    
    public static float getSpeed() {
        return (float)Math.sqrt(MoveUtil.mc.thePlayer.motionX * MoveUtil.mc.thePlayer.motionX + MoveUtil.mc.thePlayer.motionZ * MoveUtil.mc.thePlayer.motionZ);
    }
    
    public static void setMotionWithValues(final Event em, final double speed, float yaw, double forward, double strafe) {
        if (forward == 0.0 && strafe == 0.0) {
            MoveUtil.mc.thePlayer.motionX = 0.0;
            MoveUtil.mc.thePlayer.motionZ = 0.0;
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            MoveUtil.mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
            MoveUtil.mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }
    
    public static boolean isMovingWithKeys() {
        return Keyboard.isKeyDown(MoveUtil.mc.gameSettings.keyBindForward.getKeyCode()) || Keyboard.isKeyDown(MoveUtil.mc.gameSettings.keyBindBack.getKeyCode()) || Keyboard.isKeyDown(MoveUtil.mc.gameSettings.keyBindLeft.getKeyCode()) || Keyboard.isKeyDown(MoveUtil.mc.gameSettings.keyBindRight.getKeyCode());
    }
    
    public static void setSpeed(final double moveSpeed) {
        setSpeed(moveSpeed, MoveUtil.mc.thePlayer.rotationYaw, MoveUtil.mc.thePlayer.movementInput.moveStrafe, MoveUtil.mc.thePlayer.movementInput.moveForward);
    }
    
    public double getTickDist() {
        final double xDist = MoveUtil.mc.thePlayer.posX - MoveUtil.mc.thePlayer.lastTickPosX;
        final double zDist = MoveUtil.mc.thePlayer.posZ - MoveUtil.mc.thePlayer.lastTickPosZ;
        return Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(zDist, 2.0));
    }
    
    public static void stop() {
        MoveUtil.mc.thePlayer.motionZ = 0.0;
        MoveUtil.mc.thePlayer.motionX = 0.0;
    }
    
    public static double getJumpMotion(float motionY) {
        final Potion potion = Potion.jump;
        if (MoveUtil.mc.thePlayer.isPotionActive(potion)) {
            final int amplifier = MoveUtil.mc.thePlayer.getActivePotionEffect(potion).getAmplifier();
            motionY += (amplifier + 1) * 0.1f;
        }
        return motionY;
    }
}
