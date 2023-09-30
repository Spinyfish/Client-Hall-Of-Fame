/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.script.wrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;

public class ScriptPlayer {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendMessage(String message) {
        PlayerUtil.sendClientMessage(message);
    }

    public static void setSpeed(double speed) {
        Wrapper.getMonsoon().getPlayer().setSpeed(speed);
    }

    public static void jump() {
        ScriptPlayer.mc.thePlayer.jump();
    }

    public static boolean isOnGround() {
        return ScriptPlayer.mc.thePlayer.onGround;
    }

    public static boolean isCollidedHorizontally() {
        return ScriptPlayer.mc.thePlayer.isCollidedHorizontally;
    }

    public static boolean isCollidedVertically() {
        return ScriptPlayer.mc.thePlayer.isCollidedVertically;
    }

    public static float getYaw() {
        return ScriptPlayer.mc.thePlayer.rotationYaw;
    }

    public static float getPitch() {
        return ScriptPlayer.mc.thePlayer.rotationPitch;
    }

    public static double getMotionX() {
        return ScriptPlayer.mc.thePlayer.motionX;
    }

    public static double getMotionY() {
        return ScriptPlayer.mc.thePlayer.motionY;
    }

    public static double getMotionZ() {
        return ScriptPlayer.mc.thePlayer.motionZ;
    }

    public static void setMotionX(double motion) {
        ScriptPlayer.mc.thePlayer.motionX = motion;
    }

    public static void setMotionY(double motion) {
        ScriptPlayer.mc.thePlayer.motionY = motion;
    }

    public static void setMotionZ(double motion) {
        ScriptPlayer.mc.thePlayer.motionZ = motion;
    }

    public static double getPosX() {
        return ScriptPlayer.mc.thePlayer.posX;
    }

    public static double getPosY() {
        return ScriptPlayer.mc.thePlayer.posY;
    }

    public static double getPosZ() {
        return ScriptPlayer.mc.thePlayer.posZ;
    }

    public static void setPosition(double x, double y, double z) {
        ScriptPlayer.mc.thePlayer.setPosition(x, y, z);
    }

    public static boolean isFlying() {
        return ScriptPlayer.mc.thePlayer.capabilities.isFlying;
    }

    public static void setFlying(boolean flying) {
        ScriptPlayer.mc.thePlayer.capabilities.isFlying = flying;
    }

    public static void attack(EntityLivingBase entityLivingBase) {
        ScriptPlayer.mc.thePlayer.attackTargetEntityWithCurrentItem(entityLivingBase);
    }

    public static double getDistanceToEntity(EntityLivingBase entityLivingBase) {
        return ScriptPlayer.mc.thePlayer.getDistanceToEntity(entityLivingBase);
    }

    public static void sendPacket(Packet packet) {
        PacketUtil.sendPacket(packet);
    }

    public static void sendPacketNoEvent(Packet packet) {
        PacketUtil.sendPacketNoEvent(packet);
    }

    public static double getFallDistance() {
        return ScriptPlayer.mc.thePlayer.fallDistance;
    }

    public static void setFallDistance(float fallDisatcne) {
        ScriptPlayer.mc.thePlayer.fallDistance = fallDisatcne;
    }

    public static boolean isMoving() {
        return Wrapper.getMonsoon().getPlayer().isMoving();
    }

    public static double getSpeed() {
        return Wrapper.getMonsoon().getPlayer().getSpeed();
    }

    public static double getBaseSpeed() {
        return Wrapper.getMonsoon().getPlayer().getBaseMoveSpeed();
    }
}

